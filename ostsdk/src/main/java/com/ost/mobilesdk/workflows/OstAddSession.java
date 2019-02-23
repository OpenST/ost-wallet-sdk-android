package com.ost.mobilesdk.workflows;

import android.os.Bundle;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.biometric.OstBiometricAuthentication;
import com.ost.mobilesdk.models.entities.OstDeviceManager;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;
import com.ost.mobilesdk.models.entities.OstSession;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.EIP712;
import com.ost.mobilesdk.utils.GnosisSafe;
import com.ost.mobilesdk.utils.OstPayloadBuilder;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstPollingService;
import com.ost.mobilesdk.workflows.services.OstSessionPollingService;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Keys;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;

/**
 * To Add Session
 * 1. param validation
 * 2. user activated and device authorized
 * 3. create session keys
 * 4. create payload
 * 5. api post call
 * 6. polling
 */
public class OstAddSession extends OstBaseWorkFlow implements OstPinAcceptInterface {

    private static final String TAG = "OstAddSession";
    private final String mSpendingLimit;
    private final long mExpiresAfterInSecs;
    private int mPinAskCount = 0;

    private enum STATES {
        INITIAL,
        PIN_ENTERED,
        CANCELLED,
        AUTHENTICATED
    }

    private STATES mCurrentState = STATES.INITIAL;
    private Object mStateObject = null;

    private void setFlowState(STATES currentState, Object stateObject) {
        this.mCurrentState = currentState;
        this.mStateObject = stateObject;
    }

    public OstAddSession(String userId, String spendingLimit, long expiresAfterInSecs, OstWorkFlowCallback callback) {
        super(userId, callback);
        mSpendingLimit = spendingLimit;
        mExpiresAfterInSecs = expiresAfterInSecs;
    }

    synchronized public AsyncStatus process() {
        switch (mCurrentState) {
            case INITIAL:

                Log.d(TAG, String.format("Add Session workflow for userId: %s started", mUserId));

                Log.i(TAG, "Validating user Id");
                if (!hasValidParams()) {
                    Log.e(TAG, String.format("Invalid params for userId : %s", mUserId));
                    return postErrorInterrupt("wf_as_pr_1", OstErrors.ErrorCode.INVALID_WORKFLOW_PARAMS);
                }

                Log.i(TAG, "Loading device and user entities");
                AsyncStatus status = super.loadCurrentDevice();
                status = status.isSuccess() ? super.loadUser() : status;
                status = status.isSuccess() ? super.loadToken() : status;

                if (!status.isSuccess()) {
                    Log.e(TAG, String.format("Fetching of basic entities failed for user id: %s", mUserId));
                    return status;
                }

                Log.i(TAG, "Validate states");
                if (!hasActivatedUser()) {
                    Log.e(TAG, String.format("User is not activated of user id: %s", mUserId));
                    return postErrorInterrupt("wf_as_pr_2", OstErrors.ErrorCode.USER_NOT_ACTIVATED);
                }
                if (!hasAuthorizedDevice()) {
                    Log.e(TAG, String.format("Device is not authorized of user id: %s", mUserId));
                    return postErrorInterrupt("wf_as_pr_3", OstErrors.ErrorCode.DEVICE_UNREGISTERED);
                }

                Log.i(TAG, "Ask for authentication");
                if (shouldAskForBioMetric()) {
                    new OstBiometricAuthentication(OstSdk.getContext(), getBioMetricCallBack());
                } else {
                    postGetPin(OstAddSession.this);
                }
                break;
            case PIN_ENTERED:
                Log.i(TAG, "Pin Entered");
                String[] strings = ((String) mStateObject).split(" ");
                String uPin = strings[0];
                String appSalt = strings[0];
                if (validatePin(uPin, appSalt)) {
                    Log.d(TAG, "Pin Validated");
                    postPinValidated();
                } else {
                    mPinAskCount = mPinAskCount + 1;
                    if (mPinAskCount > OstConstants.MAX_PIN_LIMIT) {
                        Log.d(TAG, "Max pin ask limit reached");
                        return postErrorInterrupt("ef_pe_pr_2", OstErrors.ErrorCode.MAX_PIN_LIMIT_REACHED);
                    }
                    Log.d(TAG, "Pin InValidated ask for pin again");
                    return postInvalidPin(OstAddSession.this);
                }
            case AUTHENTICATED:
                return authorizeSession();

            case CANCELLED:
                Log.d(TAG, String.format("Error in Add session flow: %s", mUserId));
                postErrorInterrupt("wf_pe_pr_3", OstErrors.ErrorCode.WORKFLOW_CANCELED);
                break;
        }
        return new AsyncStatus(true);
    }


    private AsyncStatus authorizeSession() {
        String sessionAddress = new OstKeyManager(mUserId).createSessionKey();

        OstApiClient ostApiClient = new OstApiClient(mUserId);

        Log.i(TAG, "Getting current block number");
        String blockNumber = getCurrentBlockNumber(ostApiClient);
        if (null == blockNumber) {
            Log.e(TAG, "BlockNumber is null");
            return postErrorInterrupt("wf_as_pr_as_1", OstErrors.ErrorCode.BLOCK_NUMBER_API_FAILED);
        }

        OstUser ostUser = OstUser.getById(mUserId);
        String tokenHolderAddress = ostUser.getTokenHolderAddress();
        String deviceManagerAddress = ostUser.getDeviceManagerAddress();

        sessionAddress = Keys.toChecksumAddress(sessionAddress);
        tokenHolderAddress = Keys.toChecksumAddress(tokenHolderAddress);
        deviceManagerAddress = Keys.toChecksumAddress(deviceManagerAddress);

        String expiryHeight = new BigInteger(blockNumber).add(new BigInteger(String
                .valueOf(mExpiresAfterInSecs))).toString();

        try {
            JSONObject response = ostApiClient.getDeviceManager();
            OstSdk.updateWithApiResponse(response);
        } catch (IOException e) {
            Log.e(TAG, "IO Exception ");
        } catch (JSONException e) {
            Log.e(TAG, "JSONException ");
        }

        int nonce = OstDeviceManager.getById(ostUser.getDeviceManagerAddress()).getNonce();
        Log.i(TAG, String.format("Device Manager  nonce %d", nonce));
        String stringNonce = String.valueOf(nonce);

        JSONObject jsonObject = new GnosisSafe.SafeTxnBuilder()
                .setAddOwnerExecutableData(new GnosisSafe().getAuthorizeSessionExecutableData
                        (sessionAddress, mSpendingLimit, expiryHeight))
                .setToAddress(tokenHolderAddress)
                .setVerifyingContract(deviceManagerAddress)
                .setNonce(stringNonce)
                .build();

        String signature = null;
        String signerAddress = ostUser.getCurrentDevice().getAddress();
        try {
            String messageHash = new EIP712(jsonObject).toEIP712TransactionHash();
            signature = OstUser.getById(mUserId).sign(messageHash);
        } catch (Exception e) {
            Log.e(TAG, "Exception in toEIP712TransactionHash");
            return postErrorInterrupt("wf_as_pr_as_2", OstErrors.ErrorCode.EIP712_FAILED);
        }

        Map<String, Object> map = new OstPayloadBuilder()
                .setDataDefination(OstDeviceManagerOperation.KIND_TYPE.AUTHORIZE_SESSION.toUpperCase())
                .setRawCalldata(new GnosisSafe().getAuthorizeSessionData(sessionAddress, mSpendingLimit, expiryHeight))
                .setCallData(new GnosisSafe().getAuthorizeSessionExecutableData(sessionAddress, mSpendingLimit, expiryHeight))
                .setTo(tokenHolderAddress)
                .setSignatures(signature)
                .setSigners(Arrays.asList(signerAddress))
                .setNonce(stringNonce)
                .build();

        JSONObject responseObject = null;
        try {
            responseObject = ostApiClient.postAddSession(map);
            Log.i(TAG, String.format("Response %s", responseObject.toString()));
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            return postErrorInterrupt("wf_as_pr_as_3", OstErrors.ErrorCode.ADD_DEVICE_API_FAILED);
        }
        if (!isValidResponse(responseObject)) {
            return postErrorInterrupt("Not a valid response");
        }

        Log.i(TAG, "Starting Session polling service");

        OstSessionPollingService.startPolling(mUserId, sessionAddress, OstSession.CONST_STATUS.INITIALIZING,
                OstSession.CONST_STATUS.AUTHORISED);

        Log.i(TAG, "Waiting for update");
        Bundle bundle = waitForUpdate(OstSdk.SESSION, sessionAddress);
        if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
            Log.d(TAG, String.format("Polling time out for session Id: %s", sessionAddress));
            return postErrorInterrupt("wf_ad_pr_4", OstErrors.ErrorCode.POLLING_TIMEOUT);
        }

        Log.i(TAG, "Syncing Entity: Sessions");
        new OstSdkSync(mUserId,OstSdkSync.SYNC_ENTITY.SESSION).perform();

        Log.i(TAG, "Response received for Add session");
        return postFlowComplete();
    }

    private String getCurrentBlockNumber(OstApiClient ostApiClient) {
        String blockNumber = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = ostApiClient.getCurrentBlockNumber();
        } catch (IOException e) {
            Log.e(TAG, "IOException");
        }
        blockNumber = parseResponseForKey(jsonObject, OstConstants.BLOCK_HEIGHT);
        return blockNumber;
    }

    @Override
    public void pinEntered(String uPin, String appUserPassword) {
        setFlowState(STATES.PIN_ENTERED, null);
        perform();
    }


    @Override
    public void cancelFlow(OstError ostError) {
        setFlowState(OstAddSession.STATES.CANCELLED, ostError);
        perform();
    }

    @Override
    void onBioMetricAuthenticationSuccess() {
        super.onBioMetricAuthenticationSuccess();
        setFlowState(OstAddSession.STATES.AUTHENTICATED, null);
        perform();
    }

    @Override
    void onBioMetricAuthenticationFail() {
        super.onBioMetricAuthenticationFail();
        super.onBioMetricAuthenticationFail();
        setFlowState(OstAddSession.STATES.CANCELLED, null);
        perform();
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.ADD_SESSION;
    }
}