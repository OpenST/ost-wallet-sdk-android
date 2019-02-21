package com.ost.mobilesdk.workflows;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.EIP712;
import com.ost.mobilesdk.utils.GnosisSafe;
import com.ost.mobilesdk.utils.OstPayloadBuilder;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstPollingService;

import org.json.JSONObject;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    private static final String TAG = "OstAddDevice";
    private final String mSpendingLimit;
    private final long mExpiresAfterInSecs;

    private enum STATES {
        INITIAL,
        PIN,
        ERROR
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
                    postError(String.format("Invalid params for userId : %s", mUserId));
                    return new AsyncStatus(false);
                }

                Log.i(TAG, "Validate states");
                if (!hasActivatedUser()) {
                    postError(String.format("User state is not activated for user Id: %s", mUserId));
                    return new AsyncStatus(false);
                }
                if (!hasAuthorizedDevice()) {
                    postError("Does not has authorized device");
                    return new AsyncStatus(false);
                }

                //Todo:: Ask for authentication

            case PIN:
                return authorizeSession();

            case ERROR:
                postError(String.format("Error in Add device flow: %s", mUserId));
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
            postError("BlockNumber is null");
            return new AsyncStatus(false);
        }

        OstUser ostUser = OstUser.getById(mUserId);
        String tokenHolderAddress = ostUser.getTokenHolderAddress();

        String expiryHeight = new BigInteger(blockNumber).add(new BigInteger(String
                .valueOf(mExpiresAfterInSecs))).toString();

        JSONObject jsonObject = new GnosisSafe.SafeTxnBuilder()
                .setAddOwnerExecutableData(new GnosisSafe().getAuthorizeSessionExecutableData
                        (sessionAddress, mSpendingLimit, expiryHeight))
                .setToAddress(tokenHolderAddress)
                .build();

        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        String signature = null;
        String signerAddress = ostUser.getCurrentDevice().getAddress();
        try {
            String messageHash = new EIP712(jsonObject).toEIP712TransactionHash();
            signature = ostKeyManager.sign(signerAddress,
                    Numeric.hexStringToByteArray(messageHash));
        } catch (Exception e) {
            Log.e(TAG, "Exception in toEIP712TransactionHash");
            postError("Exception in toEIP712TransactionHash");
            return new AsyncStatus(false);
        }

        Map<String, Object> map = new OstPayloadBuilder()
                .setDataDefination(OstDeviceManagerOperation.KIND_TYPE.AUTHORIZE_SESSION.toUpperCase())
                .setRawCalldata(new GnosisSafe().getAuthorizeSessionData(sessionAddress, mSpendingLimit, expiryHeight))
                .setCallData(new GnosisSafe().getAuthorizeSessionExecutableData(sessionAddress, mSpendingLimit, expiryHeight))
                .setTo(tokenHolderAddress)
                .setSignatures(signature)
                .setSigner(signerAddress)
                .build();

        JSONObject responseObject = null;
        try {
            responseObject = ostApiClient.postAddDevice(map);
            Log.i(TAG, String.format("Response %s", responseObject.toString()));
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            postError("IOException");
            return new AsyncStatus(false);
        }
        postFlowComplete();
        return new AsyncStatus(true);
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


    private boolean hasAuthorizedDevice() {
        OstDevice ostDevice = OstUser.getById(mUserId).getCurrentDevice();
        return ostDevice.getStatus().toLowerCase().equals(OstDevice.CONST_STATUS.AUTHORIZED);
    }

    private boolean hasActivatedUser() {
        OstUser ostUser = OstUser.getById(mUserId);
        return ostUser.getStatus().toLowerCase().equals(OstUser.CONST_STATUS.ACTIVATED);
    }

    private boolean waitForUpdate() {
        final boolean[] isTimeout = new boolean[1];
        isTimeout[0] = false;

        CountDownLatch countDownLatch = new CountDownLatch(1);
        BroadcastReceiver updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                Log.d(TAG, "Intent received");
                String userId = intent.getStringExtra(OstPollingService.EXTRA_USER_ID);
                String entityType = intent.getStringExtra(OstPollingService.EXTRA_ENTITY_TYPE);
                boolean isPollingTimeOut = intent.getBooleanExtra(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true);
                if (mUserId.equals(userId) && OstSdk.USER.equals(entityType)) {
                    Log.d(TAG, String.format("Got update message from polling service for device id:%s", userId));
                    if (isPollingTimeOut) {
                        Log.w(TAG, "Polling timeout reached");
                        isTimeout[0] = true;
                    }
                    countDownLatch.countDown();
                }
            }
        };
        LocalBroadcastManager.getInstance(OstSdk.getContext()).registerReceiver(updateReceiver,
                new IntentFilter(OstPollingService.ENTITY_UPDATE_MESSAGE));
        try {
            countDownLatch.await(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(OstSdk.getContext()).unregisterReceiver(updateReceiver);
        return isTimeout[0];
    }


    @Override
    public void pinEntered(String uPin, String appUserPassword) {
        setFlowState(STATES.PIN, null);
        perform();
    }


    @Override
    public void cancelFlow(OstError ostError) {
        setFlowState(OstAddSession.STATES.ERROR, ostError);
        perform();
    }
}