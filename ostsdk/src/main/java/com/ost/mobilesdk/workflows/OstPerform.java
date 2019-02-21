package com.ost.mobilesdk.workflows;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.biometric.OstBiometricAuthentication;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.EIP712;
import com.ost.mobilesdk.utils.GnosisSafe;
import com.ost.mobilesdk.utils.OstPayloadBuilder;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Device A which will add
 * 1. Scan QR code
 * 2. Sign with wallet key
 * 3. approve
 */
public class OstPerform extends OstBaseWorkFlow implements OstPinAcceptInterface {

    private static final String TAG = "OstPerform";
    private final JSONObject mPayload;
    private int mPinAskCount = 0;

    private enum STATES {
        INITIAL,
        QR_CODE,
        PIN,
        WORDS,
        CANCELLED,
        AUTHENTICATED,
        PIN_ENTERED,
    }

    private STATES mCurrentState = STATES.INITIAL;
    private Object mStateObject = null;

    private void setFlowState(STATES currentState, Object stateObject) {
        this.mCurrentState = currentState;
        this.mStateObject = stateObject;
    }

    public OstPerform(String userId, JSONObject payload, OstWorkFlowCallback callback) {
        super(userId, callback);
        mPayload = payload;
    }

    synchronized public AsyncStatus process() {
        switch (mCurrentState) {
            case INITIAL:
                Log.d(TAG, String.format("Perform workflow for userId: %s started", mUserId));

                Log.i(TAG, "Validating  payload");
                if (!validatePayload()) {
                    Log.e(TAG, String.format("payload validation failed for %s", mPayload.toString()));
                    return postErrorInterrupt("wf_pe_pr_1", OstErrors.ErrorCode.INVALID_WORKFLOW_PARAMS);
                }

                if (shouldAskForBioMetric()) {
                    new OstBiometricAuthentication(OstSdk.getContext(), getBioMetricCallBack());
                } else {
                    postGetPin(OstPerform.this);
                }
                break;
            case PIN_ENTERED:
                Log.i(TAG, "Pin Entered");
                String[] strings = ((String)mStateObject).split(" ");
                String uPin = strings[0];
                String appSalt = strings[0];
                if(validatePin(uPin, appSalt)) {
                    Log.d(TAG, "Pin Validated");
                    postPinValidated();
                } else {
                    mPinAskCount = mPinAskCount + 1;
                    if (mPinAskCount > OstConstants.MAX_PIN_LIMIT) {
                        Log.d(TAG, "Max pin ask limit reached");
                        return postErrorInterrupt("ef_pe_pr_2", OstErrors.ErrorCode.MAX_PIN_LIMIT_REACHED);
                    }
                    Log.d(TAG, "Pin InValidated ask for pin again");
                    return postInvalidPin(OstPerform.this);
                }
            case AUTHENTICATED:
                Log.i(TAG, "Determining data definition");

                String dataDefinition = getDataDefinition();
                switch (dataDefinition) {
                    case OstDeviceManagerOperation.KIND_TYPE.AUTHORIZE_DEVICE:
                        return authorizeDevice();
                    default:
                        Log.w(TAG, "Unknown data definition");
                }
                break;
            case CANCELLED:
                Log.d(TAG, String.format("Error in Add device flow: %s", mUserId));
                postErrorInterrupt("wf_pe_pr_5",OstErrors.ErrorCode.WORKFLOW_CANCELED);
                break;
        }
        return new AsyncStatus(true);
    }

    private AsyncStatus authorizeDevice() {
        JSONObject safeTxn = new GnosisSafe.SafeTxnBuilder()
                .setAddOwnerExecutableData(getCallData())
                .setToAddress(getDeviceManagerAddress())
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

        Log.i(TAG, "Api Call payload");
        try {

            Map<String, Object> map = new OstPayloadBuilder()
                    .setDataDefination(OstDeviceManagerOperation.KIND_TYPE.AUTHORIZE_DEVICE.toUpperCase())
                    .setRawCalldata(new GnosisSafe().getAddOwnerWithThresholdData(mPayload.getString(OstConstants.DEVICE_ADDRESS), "1"))
                    .setCallData(getCallData())
                    .setTo(getDeviceManagerAddress())
                    .setSignatures(signature)
                    .setSigners(Arrays.asList(signerAddress))
                    .build();
            OstApiClient ostApiClient = new OstApiClient(mUserId);
            JSONObject jsonObject = ostApiClient.postAddDevice(map);
            Log.d(TAG, String.format("JSON Object response: %s", jsonObject.toString()));
            if(isValidResponse(jsonObject)) {
                return postFlowComplete();
            } else {
                return postErrorInterrupt("Not a valid response");
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception");
            return new AsyncStatus(false);
        } catch (IOException e) {
            Log.e(TAG, "IO Exception");
            return new AsyncStatus(false);
        }
    }

    private String getDeviceManagerAddress() {
        return OstUser.getById(mUserId).getDeviceManagerAddress();
    }

    private String getCallData() {
        try {
            return new GnosisSafe().getAddOwnerWithThresholdExecutableData(mPayload
                    .getString(OstConstants.DEVICE_ADDRESS));
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected JSONException");
        }
        return null;
    }

    private boolean validatePayload() {
        boolean hasDataDefinition = mPayload.has(OstConstants.DATA_DEFINATION);
        boolean hasUserId = mPayload.has(OstConstants.USER_ID);
        boolean hasDeviceAddress = mPayload.has(OstConstants.DEVICE_ADDRESS);
        return hasDataDefinition && hasUserId && hasDeviceAddress;
    }

    private @NonNull
    String getDataDefinition() {
        try {
            return mPayload.getString(OstPayloadBuilder.DATA_DEFINATION);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected JSONException");
        }
        return "";
    }

    @Override
    public void pinEntered(String uPin, String appUserPassword) {
        setFlowState(OstPerform.STATES.PIN_ENTERED, String.format("%s %s", uPin, appUserPassword));
        perform();
    }

    @Override
    public void cancelFlow(OstError ostError) {
        setFlowState(OstPerform.STATES.CANCELLED, ostError);
        perform();
    }

    @Override
    void onBioMetricAuthenticationSuccess() {
        super.onBioMetricAuthenticationSuccess();
        setFlowState(STATES.AUTHENTICATED, null);
        perform();
    }

    @Override
    void onBioMetricAuthenticationFail() {
        super.onBioMetricAuthenticationFail();
        setFlowState(STATES.CANCELLED, null);
        perform();
    }
}