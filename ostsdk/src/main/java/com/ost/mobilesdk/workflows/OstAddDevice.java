package com.ost.mobilesdk.workflows;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Joiner;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.security.impls.OstSdkCrypto;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.QRCode;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstAddDeviceFlowInterface;
import com.ost.mobilesdk.workflows.interfaces.OstStartPollingInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstDevicePollingService;
import com.ost.mobilesdk.workflows.services.OstPollingService;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;
import java.util.List;

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
public class OstAddDevice extends OstBaseWorkFlow implements OstAddDeviceFlowInterface, OstStartPollingInterface {

    private static final String TAG = "OstAddDevice";

    private enum STATES {
        INITIAL,
        QR_CODE,
        PIN,
        WORDS,
        POLLING,
        CANCELLED
    }

    private STATES mCurrentState = STATES.INITIAL;
    private Object mStateObject = null;

    private void setFlowState(STATES currentState, Object stateObject) {
        this.mCurrentState = currentState;
        this.mStateObject = stateObject;
    }

    public OstAddDevice(String userId, OstWorkFlowCallback callback) {
        super(userId, callback);
    }

    synchronized public AsyncStatus process() {
        switch (mCurrentState) {
            case INITIAL:
                Log.d(TAG, String.format("Add Device workflow for userId: %s started", mUserId));

                Log.i(TAG, "Validating user Id");
                if (!hasValidParams()) {
                    Log.e(TAG, String.format("Invalid params for userId : %s", mUserId));
                    return postErrorInterrupt("wf_ad_pr_1", OstErrors.ErrorCode.INVALID_WORKFLOW_PARAMS);
                }

                Log.i(TAG, "Loading device and user entities");
                AsyncStatus status = super.loadCurrentDevice();
                status = status.isSuccess() ? super.loadUser() : status;

                if (!status.isSuccess()) {
                    Log.e(TAG, String.format("Fetching of basic entities failed for user id: %s", mUserId));
                    return status;
                }

                Log.i(TAG, "Validate states");
                if (!hasActivatedUser()) {
                    Log.e(TAG, String.format("User is not activated of user id: %s", mUserId));
                    return postErrorInterrupt("wf_ad_pr_2", OstErrors.ErrorCode.USER_NOT_ACTIVATED);
                }
                if (!hasRegisteredDevice()) {
                    Log.e(TAG, String.format("Device is not registered of user id: %s", mUserId));
                    return postErrorInterrupt("wf_ad_pr_3", OstErrors.ErrorCode.DEVICE_UNREGISTERED);
                }
                if (hasAuthorizingDevice()) {
                    Log.v(TAG, String.format("Device is authorizing of user id: %s  start polling", mUserId));
                    startPolling();
                    return new AsyncStatus(true);
                }

                Log.i(TAG, "Determine Add device flow");
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
                Log.d(TAG, String.format("words flow for userId: %s started", mUserId));
                List<String> wordsList = (List<String>) mStateObject;
                String mnemonics = Joiner.on(" ").join(wordsList);
                ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genECKeyFromMnemonics(mnemonics);

                try {
                    JSONObject response = mOstApiClient.getDeviceManager();
                    OstSdk.updateWithApiResponse(response);
                } catch (IOException e) {
                    Log.e(TAG, "IO Exception ");
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException ");
                }

                OstUser wordsOstUser = OstUser.getById(mUserId);
                String wordsDeviceAddress = wordsOstUser.getCurrentDevice().getAddress();
                String wordsDeviceManagerAddress = wordsOstUser.getDeviceManagerAddress();

                String eip712Hash = getEIP712Hash(wordsDeviceAddress, wordsDeviceManagerAddress);
                if (null == eip712Hash) {
                    Log.e(TAG, "EIP-712 error while parsing json object of sageTxn");
                    return postErrorInterrupt("wf_ad_pr_4", OstErrors.ErrorCode.EIP712_FAILED);
                }

                Log.i(TAG, "Signing  txnHash");
                String signature = OstKeyManager.sign(eip712Hash, ecKeyPair);
                String signerAddress = Credentials.create(ecKeyPair).getAddress();
                try {
                    JSONObject jsonObject = mOstApiClient.getDevices(signerAddress);
                    OstSdk.updateWithApiResponse(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != OstDevice.getById(signerAddress)) {
                    postWordsValidated();
                    AsyncStatus apiCallStatus = makeAddDeviceCall(signature, signerAddress, wordsDeviceManagerAddress, wordsDeviceAddress);
                    if (apiCallStatus.isSuccess()) {
                        startPolling();
                    } else {
                        return postErrorInterrupt("wf_ad_pr_4", OstErrors.ErrorCode.ADD_DEVICE_API_FAILED);
                    }
                } else {
                    postInvalidWords();
                }
                break;
            case POLLING:
                Log.i(TAG, "Starting Device polling service");
                OstUser ostUser = OstUser.getById(mUserId);
                String deviceAddress = ostUser.getCurrentDevice().getAddress();
                OstDevicePollingService.startPolling(mUserId, deviceAddress, OstDevice.CONST_STATUS.AUTHORIZING,
                        OstDevice.CONST_STATUS.AUTHORIZED);

                Log.i(TAG, "Waiting for update");
                Bundle bundle = waitForUpdate(OstSdk.DEVICE, deviceAddress);
                if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
                    Log.d(TAG, String.format("Polling time out for device Id: %s", deviceAddress));
                    return postErrorInterrupt("wf_ad_pr_4", OstErrors.ErrorCode.POLLING_TIMEOUT);
                }

                Log.i(TAG, "Syncing Entity: Device");
                new OstSdkSync(mUserId, OstSdkSync.SYNC_ENTITY.DEVICE).perform();

                Log.i(TAG, "Response received for Add device");
                postFlowComplete();
                break;
            case CANCELLED:
                Log.d(TAG, String.format("Error in Add device flow: %s", mUserId));
                postErrorInterrupt("wf_ad_pr_5", OstErrors.ErrorCode.WORKFLOW_CANCELED);
                break;
        }
        return new AsyncStatus(true);
    }

    private AsyncStatus postInvalidWords() {
        Log.i(TAG, "post Invalid words");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.invalidWalletWords(OstAddDevice.this);
            }
        });
        return new AsyncStatus(true);
    }

    private AsyncStatus postWordsValidated() {
        Log.i(TAG, "post Words validated");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.walletWordsValidated();
            }
        });
        return new AsyncStatus(true);
    }

    private boolean hasAuthorizingDevice() {
        OstDevice ostDevice = OstUser.getById(mUserId).getCurrentDevice();
        return ostDevice.getStatus().equalsIgnoreCase(OstDevice.CONST_STATUS.AUTHORIZING);
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
                mCallback.showQR(qrPayLoad, OstAddDevice.this);
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
        return ostDevice.getStatus().equalsIgnoreCase(OstDevice.CONST_STATUS.REGISTERED) ||
                ostDevice.getStatus().equalsIgnoreCase(OstDevice.CONST_STATUS.AUTHORIZED) ||
                ostDevice.getStatus().equalsIgnoreCase(OstDevice.CONST_STATUS.AUTHORIZING);
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
        setFlowState(STATES.WORDS, wordList);
        perform();
    }

    @Override
    public void cancelFlow(OstError ostError) {
        setFlowState(OstAddDevice.STATES.CANCELLED, ostError);
        perform();
    }

    @Override
    public void startPolling() {
        setFlowState(STATES.POLLING, null);
        perform();
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.ADD_DEVICE;
    }
}