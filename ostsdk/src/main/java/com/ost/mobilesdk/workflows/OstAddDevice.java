package com.ost.mobilesdk.workflows;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.QRCode;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstAddDeviceFlowInterface;
import com.ost.mobilesdk.workflows.interfaces.OstStartPollingInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstDevicePollingService;
import com.ost.mobilesdk.workflows.services.OstPollingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * To Add device using QR
 * Device B to be added
 * 1.Validations
 *  1.1 Device should be registered
 *  1.2 User should be Activated.
 * 2. Ask App for flow
 *  2.1 QR Code
 *      2.1.1 generate multi sig code
 *      2.1.2 start polling
 *  2.2 Pin(Recovery address)
 *  2.3 12 Words
 *
 *
 * Device A which will add
 * 1. Scan QR code
 * 2. Sign with wallet key
 * 3. approve
 */
public class OstAddDevice extends OstBaseWorkFlow implements OstAddDeviceFlowInterface, OstStartPollingInterface {

    private static final String TAG = "OstAddDevice";

    private enum STATES {
        INITIAL,
        QR_CODE,
        PIN,
        WORDS,
        POLLING,
        ERROR
    }

    private STATES mCurrentState = STATES.INITIAL;
    private Object mStateObject = null;

    private void setFlowState(STATES currentState, Object stateObject) {
        this.mCurrentState = currentState;
        this.mStateObject = stateObject;
    }

    public OstAddDevice(String userId, Handler handler, OstWorkFlowCallback callback) {
        super(userId, handler, callback);
    }

    synchronized public AsyncStatus process() {
        switch (mCurrentState) {
            case INITIAL:
                Log.d(TAG, String.format("Add Device workflow for userId: %s started", mUserId));

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
                if (!hasRegisteredDevice()) {
                    postError("Does not has registered device");
                    return new AsyncStatus(false);
                }

                Log.i(TAG,"Determine Add device flow");
                determineAddDeviceFlow();
                break;
            case QR_CODE:

                Log.d(TAG, String.format("QR Code add device flow for userId: %s started", mUserId));
                //Create payload
                String payload = createPayload();
                Bitmap bitmap = createQRBitMap(payload);
                Log.i(TAG, "showing QR code");
                showPayload(bitmap);
                break;
            case PIN:
                break;
            case WORDS:
                break;
            case POLLING:
                Log.i(TAG, "Starting Device polling service");
                OstUser ostUser = OstUser.getById(mUserId);
                String deviceAddress = ostUser.getCurrentDevice().getAddress();
                OstDevicePollingService.startPolling(mUserId, deviceAddress, OstDevice.CONST_STATUS.AUTHORIZING,
                        OstDevice.CONST_STATUS.AUTHORIZED);

                Log.i(TAG, "Waiting for update");
                boolean isTimeOut = waitForUpdate();
                if (isTimeOut) {
                    Log.d(TAG, String.format("Polling time out for device Id: %s", deviceAddress));
                    postError("Polling Time out");
                    return new AsyncStatus(false);
                }

                Log.i(TAG, "Response received for Add device");
                postFlowComplete();
                break;
            case ERROR:
                postError(String.format("Error in Add device flow: %s", mUserId));
                break;
        }
        return new AsyncStatus(true);
    }

    private Bitmap createQRBitMap(String payload) {
        return QRCode.newInstance(OstSdk.getContext())
                .setContent(payload)
                .setErrorCorrectionLevel(ErrorCorrectionLevel.M)
                .setMargin(2)
                .getQRCOde();
    }


    private String createPayload() {
        OstUser ostUser = OstUser.getById(mUserId);
        OstDevice ostDevice = ostUser.getCurrentDevice();
        String ownerAddress = ostDevice.getAddress();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OstConstants.DATA_DEFINATION, OstDeviceManagerOperation.KIND_TYPE.AUTHORIZE_DEVICE);
            jsonObject.put(OstConstants.USER_ID, mUserId);
            jsonObject.put(OstConstants.DEVICE_ADDRESS, ownerAddress);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected exception in createPayload");
        }

        return jsonObject.toString();
    }

    private void showPayload(final Bitmap qrPayLoad) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.showQR( qrPayLoad, OstAddDevice.this);
            }
        });
    }

    private void determineAddDeviceFlow() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.determineAddDeviceWorkFlow(OstAddDevice.this);
            }
        });
    }


    private boolean hasRegisteredDevice() {
        OstDevice ostDevice = OstUser.getById(mUserId).getCurrentDevice();
        return ostDevice.getStatus().toLowerCase().equals(OstDevice.CONST_STATUS.REGISTERED);
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
    public void QRCodeFlow() {
        setFlowState(STATES.QR_CODE,null);
        perform();
    }

    @Override
    public void pinEntered(String uPin, String appUserPassword) {
        setFlowState(STATES.PIN, null);
        perform();
    }

    @Override
    public void walletWordsEntered(List<String> wordList) {
        setFlowState(STATES.WORDS, null);
        perform();
    }

    @Override
    public void cancelFlow(OstError ostError) {
        setFlowState(OstAddDevice.STATES.ERROR, ostError);
        perform();
    }

    @Override
    public void startPolling() {
        setFlowState(STATES.POLLING, null);
        perform();
    }
}