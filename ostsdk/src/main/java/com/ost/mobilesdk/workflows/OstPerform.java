package com.ost.mobilesdk.workflows;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.EIP712;
import com.ost.mobilesdk.utils.GnosisSafe;
import com.ost.mobilesdk.utils.OstPayloadBuilder;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Device A which will add
 * 1. Scan QR code
 * 2. Sign with wallet key
 * 3. approve
 */
public class OstPerform extends OstBaseWorkFlow {

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
                        return authorizeDevice();
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

    private AsyncStatus authorizeDevice() {
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
        //Todo:: Ask for pin
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
                    .setSigner(signerAddress)
                    .build();
            OstApiClient ostApiClient = new OstApiClient(mUserId);
            JSONObject jsonObject = ostApiClient.postAddDevice(map);
            Log.d(TAG, String.format("JSON Object response: %s", jsonObject.toString()));

            postFlowComplete();
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception");
            return new AsyncStatus(false);
        } catch (IOException e) {
            Log.e(TAG, "IO Exception");
            return new AsyncStatus(false);
        }
        return new AsyncStatus(true);
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
        //TODO::validate owner address is registered
        return true;
    }

    private @NonNull
    String getDataDefination() {
        try {
            return mPayload.getString(OstPayloadBuilder.DATA_DEFINATION);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected JSONException");
        }
        return "";
    }
}