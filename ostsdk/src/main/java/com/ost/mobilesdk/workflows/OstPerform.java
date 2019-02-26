package com.ost.mobilesdk.workflows;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.CommonUtils;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Device A which will add
 * 1. Scan QR code
 * 2. Sign with wallet key
 * 3. approve
 */
public class OstPerform extends OstBaseWorkFlow {

    private static final String TAG = "OstPerform";
    private final JSONObject mPayload;

    public OstPerform(String userId, JSONObject payload, OstWorkFlowCallback callback) {
        super(userId, callback);
        mPayload = payload;
    }

    public AsyncStatus process() {

        Log.d(TAG, String.format("Perform workflow for userId: %s started", mUserId));

        Log.i(TAG, "Validating  payload");
        if (!validatePayload()) {
            Log.e(TAG, String.format("payload validation failed for %s", mPayload.toString()));
            return postErrorInterrupt("wf_pe_pr_1", OstErrors.ErrorCode.INVALID_WORKFLOW_PARAMS);
        }
        String dataDefinition = getDataDefinition();
        JSONObject dataObject = getDataObject();
        if (OstConstants.DATA_DEFINITION_TRANSACTION.equalsIgnoreCase(dataDefinition)) {
            if (!validateTransactionData(dataObject)) {
                return postErrorInterrupt("wf_pe_pr_3", OstErrors.ErrorCode.INVALID_QR_TRANSACTION_DATA);
            }
            String ruleName = dataObject.optString(OstConstants.QR_RULE_NAME);

            JSONArray jsonArrayTokenHolderAddresses = dataObject.optJSONArray(OstConstants.QR_TOKEN_HOLDER_ADDRESSES);
            List<String> tokenHolderAddresses = new CommonUtils().jsonArrayToList(jsonArrayTokenHolderAddresses);

            JSONArray jsonArrayAmounts = dataObject.optJSONArray(OstConstants.QR_AMOUNTS);
            List<String> amounts = new CommonUtils().jsonArrayToList(jsonArrayAmounts);

            String tokenId = dataObject.optString(OstConstants.QR_TOKEN_ID);

            OstExecuteTransaction ostExecuteTransaction = new OstExecuteTransaction(mUserId, tokenId,
                    tokenHolderAddresses, amounts, ruleName, mCallback);
            ostExecuteTransaction.perform();

        } else if (OstConstants.DATA_DEFINITION_AUTHORIZE_DEVICE.equalsIgnoreCase(dataDefinition)) {
            if (!validateDeviceOperationData(dataObject)) {
                return postErrorInterrupt("wf_pe_pr_2", OstErrors.ErrorCode.INVALID_QR_DEVICE_OPERATION_DATA);
            }
            String deviceAddress = dataObject.optString(OstConstants.QR_DEVICE_ADDRESS);
            OstAddDeviceWithQR ostAddDeviceWithQR = new OstAddDeviceWithQR(mUserId, deviceAddress, mCallback);
            ostAddDeviceWithQR.perform();
            return new AsyncStatus(true);
        }

        return new AsyncStatus(false);

    }

    private boolean validateTransactionData(JSONObject dataObject) {
        boolean hasRuleName = dataObject.has(OstConstants.QR_RULE_NAME);
        boolean hasTokenHolderAddresses = dataObject.has(OstConstants.QR_TOKEN_HOLDER_ADDRESSES);
        boolean hasAmounts = dataObject.has(OstConstants.QR_AMOUNTS);
        boolean hasTokenId = dataObject.has(OstConstants.QR_TOKEN_ID);
        return hasRuleName && hasTokenHolderAddresses && hasAmounts && hasTokenId;
    }

    private boolean validateDeviceOperationData(JSONObject dataObject) {
        boolean hasDeviceAddress = dataObject.has(OstConstants.QR_DEVICE_ADDRESS);
        return hasDeviceAddress;
    }

    private AsyncStatus authorizeDevice() {
        try {
            JSONObject response = mOstApiClient.getDeviceManager();
        } catch (IOException e) {
            return postErrorInterrupt("wf_ad_pr_4", OstErrors.ErrorCode.ADD_DEVICE_API_FAILED);
        }

        String deviceAddress = getDeviceAddress();
        String deviceManagerAddress = getDeviceManagerAddress();

        String eip712Hash = getEIP712Hash(deviceAddress, deviceManagerAddress);
        if (null == eip712Hash) {
            Log.e(TAG, "EIP-712 error while parsing json object of sageTxn");
            return postErrorInterrupt("wf_ad_pr_6", OstErrors.ErrorCode.EIP712_FAILED);
        }

        Log.i(TAG, "Sign eip712Hash");
        String signature = OstUser.getById(mUserId).sign(eip712Hash);
        String signerAddress = OstUser.getById(mUserId).getCurrentDevice().getAddress();

        Log.i(TAG, "Api Call payload");
        AsyncStatus apiCallStatus = makeAddDeviceCall(signature, signerAddress, deviceManagerAddress, deviceAddress);
        if (apiCallStatus.isSuccess()) {
            return postFlowComplete();
        } else {
            return postErrorInterrupt("wf_ad_pr_7", OstErrors.ErrorCode.ADD_DEVICE_API_FAILED);
        }
    }

    private String getDeviceAddress() {
        try {
            JSONObject data = mPayload.getJSONObject(OstConstants.QR_DATA);
            return data.getString(OstConstants.QR_DEVICE_ADDRESS);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected JSONException");
        }
        return null;

    }

    private String getDeviceManagerAddress() {
        return OstUser.getById(mUserId).getDeviceManagerAddress();
    }

    private boolean validatePayload() {
        boolean hasDataDefinition = mPayload.has(OstConstants.QR_DATA_DEFINITION);
        boolean hasDataDefinitionVersion = mPayload.has(OstConstants.QR_DATA_DEFINITION_VERSION);
        boolean data = mPayload.has(OstConstants.QR_DATA);
        return hasDataDefinition && hasDataDefinitionVersion && data;
    }

    private @NonNull
    String getDataDefinition() {
        try {
            return mPayload.getString(OstConstants.QR_DATA_DEFINITION);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected JSONException");
        }
        return "";
    }

    private JSONObject getDataObject() {
        try {
            return mPayload.getJSONObject(OstConstants.QR_DATA);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected JSONException");
        }
        return null;
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.PERFORM;
    }
}