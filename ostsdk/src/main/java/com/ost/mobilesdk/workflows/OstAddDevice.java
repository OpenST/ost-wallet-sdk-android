package com.ost.mobilesdk.workflows;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstDeviceManager;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.GnosisSafe;
import com.ost.mobilesdk.utils.OstPayloadBuilder;
import com.ost.mobilesdk.utils.QRCode;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstAddDeviceFlowInterface;
import com.ost.mobilesdk.workflows.interfaces.OstStartPollingInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

import java.util.List;

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
            case ERROR:
                postError(String.format("Error in Registration flow: %s", mUserId));
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
        String deviceManagerAddress = ostUser.getDeviceManagerAddress();
        OstDeviceManager ostDeviceManager = OstDeviceManager.getById(deviceManagerAddress);
        String callData = new GnosisSafe().getAddOwnerWithThresholdExecutableData(ownerAddress);
        JSONObject rawCallData = new GnosisSafe().getJSONAddOwnerWithThresholdData(ownerAddress);

        JSONObject jsonObject = new OstPayloadBuilder()
                .setDataDefination(OstDeviceManagerOperation.KIND_TYPE.AUTHORIZE_DEVICE.toUpperCase())
                .setCallData(callData)
                .setRawCalldata(rawCallData)
                .setTo(deviceManagerAddress)
                .build();

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

    }
}