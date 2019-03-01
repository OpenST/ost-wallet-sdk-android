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
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstPollingService;
import com.ost.mobilesdk.workflows.services.OstSessionPollingService;

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
public class OstAddSession extends OstBaseUserAuthenticatorWorkflow implements OstPinAcceptInterface {

    private static final String TAG = "OstAddSession";
    private final String mSpendingLimit;
    private final long mExpiresAfterInSecs;

    public OstAddSession(String userId, String spendingLimit, long expiresAfterInSecs, OstWorkFlowCallback callback) {
        super(userId, callback);
        mSpendingLimit = spendingLimit;
        mExpiresAfterInSecs = expiresAfterInSecs;
    }



    @Override
    AsyncStatus performOnAuthenticated() {
        String sessionAddress = new OstKeyManager(mUserId).createSessionKey();

        OstApiClient ostApiClient = new OstApiClient(mUserId);

        Log.i(TAG, "Getting current block number");
        String blockNumber = getCurrentBlockNumber(ostApiClient);
        if (null == blockNumber) {
            Log.e(TAG, "BlockNumber is null");
            return postErrorInterrupt("wf_as_pr_1", OstErrors.ErrorCode.BLOCK_NUMBER_API_FAILED);
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
            ostApiClient.getDeviceManager();
        } catch (IOException e) {
            Log.e(TAG, "IO Exception ");
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
            Log.e(TAG, "Exception");
            return postErrorInterrupt("wf_as_pr_as_3", OstErrors.ErrorCode.ADD_DEVICE_API_FAILED);
        }
        if (!isValidResponse(responseObject)) {
            return postErrorInterrupt("Not a valid response");
        }
        //Request Acknowledge
        postRequestAcknowledge(new OstWorkflowContext(getWorkflowType()),
                new OstContextEntity(OstSession.getById(sessionAddress), OstSdk.SESSION));

        Log.i(TAG, "Starting Session polling service");

        OstSessionPollingService.startPolling(mUserId, sessionAddress, OstSession.CONST_STATUS.AUTHORISED,
                OstSession.CONST_STATUS.CREATED);

        Log.i(TAG, "Waiting for update");
        Bundle bundle = waitForUpdate(OstSdk.SESSION, sessionAddress);
        if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
            Log.d(TAG, String.format("Polling time out for session Id: %s", sessionAddress));
            return postErrorInterrupt("wf_as_pr_as_4", OstErrors.ErrorCode.POLLING_TIMEOUT);
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
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.ADD_SESSION;
    }
}