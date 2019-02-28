package com.ost.mobilesdk.workflows;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.biometric.OstBiometricAuthentication;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstDevicePollingService;
import com.ost.mobilesdk.workflows.services.OstPollingService;

import java.io.IOException;


public class OstAddDeviceWithQR extends OstBaseWorkFlow implements OstVerifyDataInterface, OstPinAcceptInterface {

    private static final String TAG = "OstAddDeviceWithQR";
    private final String mDeviceAddress;
    private int mPinAskCount = 0;
    private OstAddDeviceWithQR.STATES mCurrentState = OstAddDeviceWithQR.STATES.INITIAL;
    private Object mStateObject = null;

    public OstAddDeviceWithQR(String userId, String deviceAddress, OstWorkFlowCallback callback) {
        super(userId, callback);
        mDeviceAddress = deviceAddress;
    }

    private void setFlowState(OstAddDeviceWithQR.STATES currentState, Object stateObject) {
        this.mCurrentState = currentState;
        this.mStateObject = stateObject;
    }

    @Override
    protected AsyncStatus process() {
        switch (mCurrentState) {
            case INITIAL:
                Log.i(TAG, "Validating params");
                if (!hasValidParams()) {
                    return postErrorInterrupt("wf_adwq_pr_1", OstErrors.ErrorCode.INVALID_WORKFLOW_PARAMS);
                }

                if (!hasValidAddress(mDeviceAddress)) {
                    return postErrorInterrupt("wf_adwq_pr_2", OstErrors.ErrorCode.INVALID_ADD_DEVICE_ADDRESS);
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
                    return postErrorInterrupt("wf_as_pr_2", OstErrors.ErrorCode.USER_NOT_ACTIVATED);
                }
                if (!hasAuthorizedDevice()) {
                    return postErrorInterrupt("wf_as_pr_3", OstErrors.ErrorCode.DEVICE_UNAUTHORIZED);
                }

                postVerifyData(new OstContextEntity(OstDevice.getById(mDeviceAddress), OstSdk.DEVICE),
                        OstAddDeviceWithQR.this);
                break;
            case DATA_VERIFIED:
                Log.i(TAG, "Ask for authentication");
                if (shouldAskForBioMetric()) {
                    new OstBiometricAuthentication(OstSdk.getContext(), getBioMetricCallBack());
                } else {
                    postGetPin(OstAddDeviceWithQR.this);
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
                        return postErrorInterrupt("wf_adwq_pr_3", OstErrors.ErrorCode.MAX_PIN_LIMIT_REACHED);
                    }
                    Log.d(TAG, "Pin InValidated ask for pin again");
                    return postInvalidPin(OstAddDeviceWithQR.this);
                }
                break;
            case AUTHENTICATED:
                AsyncStatus apiCallStatus = authorizeDevice();
                if (!apiCallStatus.isSuccess()) {
                    return postErrorInterrupt("wf_adwq_pr_4", OstErrors.ErrorCode.ADD_DEVICE_API_FAILED);
                }
            case POLLING:
                OstDevicePollingService.startPolling(mUserId, mDeviceAddress, OstDevice.CONST_STATUS.AUTHORIZED,
                        OstDevice.CONST_STATUS.CREATED);

                Log.i(TAG, "Waiting for update");
                Bundle bundle = waitForUpdate(OstSdk.DEVICE, mDeviceAddress);
                if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
                    Log.d(TAG, String.format("Polling time out for device Id: %s", mDeviceAddress));
                    return postErrorInterrupt("wf_adwq_pr_5", OstErrors.ErrorCode.POLLING_TIMEOUT);
                }

                Log.i(TAG, "Response received for Add device");
                postFlowComplete();
                break;
            case CANCELLED:
                Log.d(TAG, String.format("Error in Add device flow: %s", mUserId));
                postErrorInterrupt("wf_adwq_pr_6", OstErrors.ErrorCode.WORKFLOW_CANCELED);
                break;

        }
        return new AsyncStatus(true);
    }

    private AsyncStatus authorizeDevice() {
        try {
            mOstApiClient.getDeviceManager();
        } catch (IOException e) {
            return postErrorInterrupt("wf_adwq_pr_7", OstErrors.ErrorCode.ADD_DEVICE_API_FAILED);
        }

        String deviceAddress = mDeviceAddress;
        String deviceManagerAddress = OstUser.getById(mUserId).getDeviceManagerAddress();

        String eip712Hash = getEIP712Hash(deviceAddress, deviceManagerAddress);
        if (null == eip712Hash) {
            Log.e(TAG, "EIP-712 error while parsing json object of sageTxn");
            return postErrorInterrupt("wf_adwq_pr_8", OstErrors.ErrorCode.EIP712_FAILED);
        }

        Log.i(TAG, "Sign eip712Hash");
        String signature = OstUser.getById(mUserId).sign(eip712Hash);
        String signerAddress = OstUser.getById(mUserId).getCurrentDevice().getAddress();

        Log.i(TAG, "Api Call payload");
        AsyncStatus apiCallStatus = makeAddDeviceCall(signature, signerAddress, deviceManagerAddress, deviceAddress);
        return apiCallStatus;
    }

    @Override
    boolean hasValidParams() {
        return super.hasValidParams() && !TextUtils.isEmpty(mDeviceAddress);
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.ADD_DEVICE_WITH_QR;
    }

    @Override
    public void pinEntered(String uPin, String appUserPassword) {
        setFlowState(OstAddDeviceWithQR.STATES.PIN_ENTERED, null);
        perform();
    }

    @Override
    public void dataVerified() {
        setFlowState(STATES.DATA_VERIFIED, null);
        perform();
    }

    @Override
    public void cancelFlow(OstError ostError) {
        setFlowState(OstAddDeviceWithQR.STATES.CANCELLED, ostError);
        perform();
    }

    @Override
    void onBioMetricAuthenticationSuccess() {
        super.onBioMetricAuthenticationSuccess();
        setFlowState(OstAddDeviceWithQR.STATES.AUTHENTICATED, null);
        perform();
    }

    @Override
    void onBioMetricAuthenticationFail() {
        super.onBioMetricAuthenticationFail();
        setFlowState(OstAddDeviceWithQR.STATES.CANCELLED, null);
        perform();
    }

    private enum STATES {
        INITIAL,
        DATA_VERIFIED,
        PIN_ENTERED,
        AUTHENTICATED,
        POLLING,
        CANCELLED
    }
}