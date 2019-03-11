package com.ost.mobilesdk.workflows;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.ecKeyInteracts.OstMultiSigSigner;
import com.ost.mobilesdk.ecKeyInteracts.structs.SignedRevokeDeviceStruct;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstDeviceManager;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.OstPayloadBuilder;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstDevicePollingService;
import com.ost.mobilesdk.workflows.services.OstPollingService;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;


public class OstRevokeDevice extends OstBaseUserAuthenticatorWorkflow {

    private static final String TAG = "OstRevokeDeviceWithQR";
    private final String mDeviceToBeRevoked;

    public OstRevokeDevice(String userId, String deviceAddress, OstWorkFlowCallback callback) {
        super(userId, callback);
        mDeviceToBeRevoked = deviceAddress;
    }

    @Override
    AsyncStatus performOnAuthenticated() {
        try {
            mOstApiClient.getDeviceManager();
        } catch (IOException e) {
            return postErrorInterrupt("wf_rd_pr_7", ErrorCode.DEVICE_MANAGER_API_FAILED);
        }

        OstDevice ostDeviceToBeRevoked = OstDevice.getById(mDeviceToBeRevoked);
        if (null == ostDeviceToBeRevoked) {
            throw new OstError("wf_rd_pr_6", ErrorCode.INVALID_REVOKE_DEVICE_ADDRESS);
        }
        String prevOwner = ostDeviceToBeRevoked.getLinkedAddress();
        OstMultiSigSigner ostMultiSigSigner = new OstMultiSigSigner(mUserId);
        SignedRevokeDeviceStruct signedData = ostMultiSigSigner.revokeDevice(mDeviceToBeRevoked, prevOwner);

        Log.i(TAG, "Api Call payload");
        AsyncStatus apiCallStatus = makeRevokeDeviceApiCall(signedData);

        if (!apiCallStatus.isSuccess()) {
            return postErrorInterrupt("wf_rd_pr_4", ErrorCode.ADD_DEVICE_API_FAILED);
        }

        //request acknowledge
        postRequestAcknowledge(new OstWorkflowContext(getWorkflowType()),
                new OstContextEntity(OstDevice.getById(mDeviceToBeRevoked), OstSdk.DEVICE));


        return pollForStatus();
    }

    private AsyncStatus makeRevokeDeviceApiCall(SignedRevokeDeviceStruct signedData) {
        Log.i(TAG, "Api Call payload");
        try {
            String deviceManagerAddress = signedData.getDeviceManagerAddress();
            Map<String, Object> map = new OstPayloadBuilder()
                    .setDataDefination(OstDeviceManagerOperation.KIND_TYPE.REVOKE_DEVICE.toUpperCase())
                    .setRawCalldata(signedData.getRawCallData())
                    .setCallData(signedData.getCallData())
                    .setTo(deviceManagerAddress)
                    .setSignatures(signedData.getSignature())
                    .setSigners(Arrays.asList(signedData.getSignerAddress()))
                    .setNonce(String.valueOf(signedData.getNonce()))
                    .build();
            OstApiClient ostApiClient = new OstApiClient(mUserId);
            JSONObject jsonObject = ostApiClient.postRevokeDevice(map);
            Log.d(TAG, String.format("JSON Object response: %s", jsonObject.toString()));
            if (isValidResponse(jsonObject)) {

                //increment nonce
                OstDeviceManager.getById(deviceManagerAddress).incrementNonce();

                return new AsyncStatus(true);
            } else {
                return new AsyncStatus(false);
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Exception");
            return new AsyncStatus(false);
        }
    }

    private AsyncStatus pollForStatus() {
        Log.i(TAG, "Waiting for update");
        Bundle bundle = OstDevicePollingService.startPolling(mUserId, mDeviceToBeRevoked, OstDevice.CONST_STATUS.REVOKED,
                OstDevice.CONST_STATUS.AUTHORIZED);
        if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
            Log.d(TAG, String.format("Polling time out for device Id: %s", mDeviceToBeRevoked));
            return postErrorInterrupt("wf_rd_pr_5", ErrorCode.POLLING_TIMEOUT);
        }

        Log.i(TAG, "Response received for Add device");
        return postFlowComplete();
    }

    @Override
    void ensureValidParams() {
        if (TextUtils.isEmpty(mDeviceToBeRevoked) || !WalletUtils.isValidAddress(mDeviceToBeRevoked)) {
            throw new OstError("wf_rd_evp_1", ErrorCode.INVALID_WORKFLOW_PARAMS);
        }

        if (!hasValidAddress(mDeviceToBeRevoked)) {
            throw new OstError("wf_rd_evp_2", ErrorCode.INVALID_ADD_DEVICE_ADDRESS);
        }

        super.ensureValidParams();
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.ADD_DEVICE_WITH_QR;
    }

    static class RevokeDeviceDataDefinitionInstance implements OstPerform.DataDefinitionInstance {
        private static final String TAG = "RevokeDeviceDDInstance";
        private final JSONObject dataObject;
        private final String userId;
        private final OstWorkFlowCallback callback;

        public RevokeDeviceDataDefinitionInstance(JSONObject dataObject, String userId, OstWorkFlowCallback callback) {
            this.dataObject = dataObject;
            this.userId = userId;
            this.callback = callback;
        }

        @Override
        public void validateDataPayload() {
            boolean hasDeviceAddress = dataObject.has(OstConstants.QR_DEVICE_ADDRESS);
            if (!hasDeviceAddress) {
                throw new OstError("wf_pe_pr_2", ErrorCode.INVALID_QR_DEVICE_OPERATION_DATA);
            }
        }

        @Override
        public void validateDataParams() {

        }

        @Override
        public OstContextEntity getContextEntity() {
            String deviceAddress = dataObject.optString(OstConstants.QR_DEVICE_ADDRESS);
            OstDevice ostDevice = OstDevice.getById(deviceAddress);
            OstContextEntity contextEntity = new OstContextEntity(ostDevice, OstSdk.DEVICE);
            return contextEntity;
        }

        private JSONObject updateJSONKeys(JSONObject dataObject) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(OstConstants.DEVICE_ADDRESS, dataObject.optString(OstConstants.QR_DEVICE_ADDRESS));
            } catch (JSONException e) {
                Log.e(TAG, "JSON Exception in updateJSONKeys: ", e);
            }
            return jsonObject;
        }

        @Override
        public void startDataDefinitionFlow() {
            String deviceAddress = dataObject.optString(OstConstants.QR_DEVICE_ADDRESS);
            OstRevokeDevice ostRevokeDevice = new OstRevokeDevice(userId, deviceAddress, callback);
            ostRevokeDevice.perform();
        }

        @Override
        public void validateApiDependentParams() {
            String deviceAddress = dataObject.optString(OstConstants.QR_DEVICE_ADDRESS);
            try {
                new OstApiClient(userId).getDevice(deviceAddress);
            } catch (IOException e) {
                throw new OstError("wf_pe_rd_3", ErrorCode.GET_DEVICE_API_FAILED);
            }
            if (null == OstDevice.getById(deviceAddress)) {
                throw new OstError("wf_pe_rd_4", ErrorCode.DEVICE_CAN_NOT_BE_REVOKED);
            }
            if (!OstDevice.getById(deviceAddress).canBeRevoked()) {
                throw new OstError("wf_pe_rd_5", ErrorCode.DEVICE_CAN_NOT_BE_REVOKED);
            }
        }
    }
}