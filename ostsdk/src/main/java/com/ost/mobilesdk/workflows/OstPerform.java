package com.ost.mobilesdk.workflows;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstDeviceManager;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.EIP712;
import com.ost.mobilesdk.utils.GnosisSafe;
import com.ost.mobilesdk.utils.OstPayloadBuilder;
import com.ost.mobilesdk.utils.QRCode;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstAddDeviceFlowInterface;
import com.ost.mobilesdk.workflows.interfaces.OstStartPollingInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * To Add device using QR
 * Device B to be added
 * 1.Validations
 * 1.1 Device should be registered
 * 1.2 User should be Activated.
 * 2. Ask App for flow
 * 2.1 QR Code
 * 2.1.1 generate multi sig code
 * 2.1.2 start polling
 * 2.2 Pin(Recovery address)
 * 2.3 12 Words
 * <p>
 * <p>
 * Device A which will add
 * 1. Scan QR code
 * 2. Sign with wallet key
 * 3. approve
 */
public class OstPerform extends OstBaseWorkFlow implements OstAddDeviceFlowInterface, OstStartPollingInterface {

    private static final String TAG = "OstPerform";
    private final JSONObject mPayload;

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

    public OstPerform(String userId, JSONObject payload, Handler handler, OstWorkFlowCallback callback) {
        super(userId, handler, callback);
        mPayload = payload;
    }

    synchronized public AsyncStatus process() {
        switch (mCurrentState) {
            case INITIAL:
                Log.d(TAG, String.format("Perform workflow for userId: %s started", mUserId));

                Log.i(TAG, "Validating  payload");
                if (!validatePayload()) {
                    postError("payload validation failed");
                    return new AsyncStatus(false);
                }
                Log.i(TAG, "Determining data defintion");

                String dataDefination = getDataDefination();
                switch (dataDefination) {
                    case OstDeviceManagerOperation.KIND_TYPE.AUTHORIZE_DEVICE:
                        JSONObject safeTxn = new GnosisSafe.SafeTxnBuilder()
                                .setAddOwnerExecutableData(getCallData())
                                .setDeviceManagerAddress(getDeviceManagerAddress())
                                .build();

                        //EIP-712
                        Log.i(TAG, "Performing EIP712 encoding ");
                        Log.d(TAG, String.format("String to be encoded  %s", safeTxn.toString()));
                        String safeTxnEIP712Hash = null;
                        try {
                            safeTxnEIP712Hash = new EIP712(safeTxn).toEIP712TransactionHash();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "EIP-712 error while parsing json object of sageTxn");
                            return new AsyncStatus(false);
                        }

                        Log.i(TAG, "Updating payload");
                        String signature = OstUser.getById(mUserId).sign(safeTxnEIP712Hash);
                        String signerAddress = OstUser.getById(mUserId).getCurrentDevice().getAddress();
                        updatePayload(signature, signerAddress);

                        Log.i(TAG, "Api Call payload");
                        try {
                            Map<String,Object> map = OstPayloadBuilder.getPayloadMap(mPayload);
                            OstApiClient ostApiClient = new OstApiClient(mUserId);
                            JSONObject jsonObject = ostApiClient.postAddDevice(map);
                            Log.d(TAG, String.format("JSON Object response: %s", jsonObject.toString()));

                            postFlowComplete();
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Exception");
                        } catch (IOException e) {
                            Log.e(TAG, "IO Exception");
                        }

                        break;
                    default:
                        Log.w(TAG, "Unknown data defination");
                }

                break;
            case ERROR:
                postError(String.format("Error in Registration flow: %s", mUserId));
                break;
        }
        return new AsyncStatus(true);
    }

    private JSONObject updatePayload(String signature, String signerAddress) {
        try {
            mPayload.put(OstPayloadBuilder.SIGNATURES,signature);
            mPayload.put(OstPayloadBuilder.SIGNER,signerAddress);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected JSONException");
        }
        return mPayload;
    }

    private String getDeviceManagerAddress() {
        try {
            return mPayload.getString(OstPayloadBuilder.TO).toLowerCase();
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected JSONException");
        }
        return null;
    }

    private String getCallData() {
        try {
            return mPayload.getString(OstPayloadBuilder.CALL_DATA).toLowerCase();
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected JSONException");
        }
        return null;
    }

    private boolean validatePayload() {
        return true;
    }

    private @NonNull String getDataDefination() {
        try {
            return mPayload.getString(OstPayloadBuilder.DATA_DEFINATION).toLowerCase();
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected JSONException");
        }
        return "";
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

//        JSONObject safeTxn = new GnosisSafe.SafeTxnBuilder()
//                .setAddOwnerExecutableData(callData)
//                .setDeviceManagerAddress(deviceManagerAddress)
//                .setNonce(ostDeviceManager.getNonce())
//                .build();
//
//        //EIP-712
//        Log.i(TAG, "Performing EIP712 encoding");
//        String safeTxnEIP712Hash;
//        try {
//           safeTxnEIP712Hash =  new EIP712(safeTxn).toEIP712TransactionHash();
//        } catch (Exception e) {
//            Log.e(TAG, "EIP-712 error while parsing json object of sageTxn");
//        }
    }

    private void showPayload(final Bitmap qrPayLoad) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.showQR(qrPayLoad, OstPerform.this);
            }
        });
    }

    private void determineAddDeviceFlow() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.determineAddDeviceWorkFlow(OstPerform.this);
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
        setFlowState(STATES.QR_CODE, null);
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
        setFlowState(OstPerform.STATES.ERROR, ostError);
        perform();
    }

    @Override
    public void startPolling() {

    }
}