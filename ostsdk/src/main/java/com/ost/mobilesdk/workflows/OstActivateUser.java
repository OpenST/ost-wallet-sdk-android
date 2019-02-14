package com.ost.mobilesdk.workflows;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.security.impls.OstSdkCrypto;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstPollingService;
import com.ost.mobilesdk.workflows.services.OstUserPollingService;

import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class OstActivateUser extends OstBaseWorkFlow {

    private static final String TAG = "IDPFlow";
    private static final int THREE_TIMES = 3;
    private final String mPassWord;
    private final String mUPin;
    private final String mExpirationHeight;
    private final String mSpendingLimit;

    public OstActivateUser(String userId, String uPin, String password, String expirationHeight,
                           String spendingLimit, Handler handler, OstWorkFlowCallback callback) {
        super(userId, handler, callback);
        mUPin = uPin;
        mPassWord = password;
        mExpirationHeight = expirationHeight;
        mSpendingLimit = spendingLimit;
    }

    @Override
    protected AsyncStatus process() {
        if (!hasValidParams()) {
            Log.i(TAG, "Work flow has invalid params");
            postError("Work flow has invalid params");
            return new AsyncStatus(false);
        }
        if (hasUnRegisteredDevice()) {
            Log.i(TAG, "Device is not registered");
            postError("Device is not registered");
            return new AsyncStatus(false);
        }
        if (hasActivatedUser()) {
            Log.i(TAG, "User is already activated");
            postFlowComplete();
        } else {
            //Todo :: get salt from Kit /users/{user_id}/recovery-keys
            //Todo :: backup  recoveryAddress on kit. calls endpoint /devices/back-up
            /****************Deferred code*****************/
            String salt = "salt";
            String recoveryAddress = createRecoveryKey(salt);
            /*********************************************/

            Log.i(TAG, "Creating session key");
            String sessionAddress = new OstKeyManager(mUserId).createSessionKey();

            Log.i(TAG, "Deploying token holder");
            Log.d(TAG, String.format("Deploying token with SessionAddress: %s, ExpirationHeight: %s," +
                            " SpendingLimit: %s, RecoveryAddress: %s", sessionAddress,
                    mExpirationHeight, mSpendingLimit, recoveryAddress));
            JSONObject response;
            try {
                response = new OstApiClient(mUserId).postTokenDeployment(sessionAddress,
                        mExpirationHeight, mSpendingLimit, recoveryAddress);
                OstSdk.parse(response);
            } catch (Exception e) {
                postError("Exception in post token deployment");
                return new AsyncStatus(false);
            }

            Log.i(TAG, "Starting user polling service");
            OstUserPollingService.startPolling(mUserId, mUserId, OstUser.CONST_STATUS.ACTIVATING,
                    OstUser.CONST_STATUS.ACTIVATED);

            Log.i(TAG, "Waiting for update");
            boolean isTimeOut = waitForUpdate();
            if (isTimeOut) {
                Log.d(TAG, String.format("Polling time out for user Id: %s", mUserId));
                postError("Polling Time out");
                return new AsyncStatus(false);
            }

            Log.i(TAG, "Response received for post Token deployment");
            postFlowComplete();

        }
        return new AsyncStatus(true);
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
                    Log.d(TAG, String.format("Got update message from polling service for user id:%s", userId));
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
    boolean hasValidParams() {
        return super.hasValidParams() && !TextUtils.isEmpty(mUPin) && !TextUtils.isEmpty(mPassWord)
                && !TextUtils.isEmpty(mExpirationHeight) && !TextUtils.isEmpty(mSpendingLimit);
    }

    private boolean hasActivatedUser() {
        return OstUser.CONST_STATUS.ACTIVATED.equals(OstSdk.getUser(mUserId).getStatus());
    }


    private String createRecoveryKey(String salt) {
        byte[] hashPassword = OstSdkCrypto.getInstance().genDigest(mPassWord.getBytes(), THREE_TIMES);
        byte[] scryptInput = ((new String(hashPassword)) + mUPin + mUserId).getBytes();
        byte[] seed = OstSdkCrypto.getInstance().genSCryptKey(scryptInput, salt.getBytes());

        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        //Don't store key of recovery key
        String address = ostKeyManager.createHDKey(seed);
        return address;
    }
}