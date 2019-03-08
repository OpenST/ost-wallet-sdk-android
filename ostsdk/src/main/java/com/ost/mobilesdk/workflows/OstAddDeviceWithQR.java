package com.ost.mobilesdk.workflows;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.security.OstMultiSigSigner;
import com.ost.mobilesdk.security.structs.SignedAddDeviceStruct;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstDevicePollingService;
import com.ost.mobilesdk.workflows.services.OstPollingService;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;


public class OstAddDeviceWithQR extends OstBaseUserAuthenticatorWorkflow {

    private static final String TAG = "OstAddDeviceWithQR";
    private final String mDeviceAddressToBeAdded;

    public OstAddDeviceWithQR(String userId, String deviceAddress, OstWorkFlowCallback callback) {
        super(userId, callback);
        mDeviceAddressToBeAdded = deviceAddress;
    }

    @Override
    AsyncStatus performOnAuthenticated() {
        try {
            mOstApiClient.getDeviceManager();
        } catch (IOException e) {
            return postErrorInterrupt("wf_adwq_pr_7", ErrorCode.ADD_DEVICE_API_FAILED);
        }

        OstMultiSigSigner ostMultiSigSigner = new OstMultiSigSigner(mUserId);
        SignedAddDeviceStruct signedData = ostMultiSigSigner.addExternalDevice(mDeviceAddressToBeAdded);

        Log.i(TAG, "Api Call payload");
        AsyncStatus apiCallStatus = makeAddDeviceCall(signedData);

        if (!apiCallStatus.isSuccess()) {
            return postErrorInterrupt("wf_adwq_pr_4", ErrorCode.ADD_DEVICE_API_FAILED);
        }

        //request acknowledge
        postRequestAcknowledge(new OstWorkflowContext(getWorkflowType()),
                new OstContextEntity(OstDevice.getById(mDeviceAddressToBeAdded), OstSdk.DEVICE));


        return pollForStatus();
    }

    private AsyncStatus pollForStatus() {
        Log.i(TAG, "Waiting for update");
        Bundle bundle = OstDevicePollingService.startPolling(mUserId, mDeviceAddressToBeAdded, OstDevice.CONST_STATUS.AUTHORIZED,
                OstDevice.CONST_STATUS.CREATED);
        if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
            Log.d(TAG, String.format("Polling time out for device Id: %s", mDeviceAddressToBeAdded));
            return postErrorInterrupt("wf_adwq_pr_5", ErrorCode.POLLING_TIMEOUT);
        }

        Log.i(TAG, "Response received for Add device");
        return postFlowComplete();
    }

    @Override
    void ensureValidParams() {
        if ( TextUtils.isEmpty(mDeviceAddressToBeAdded) || !WalletUtils.isValidAddress(mDeviceAddressToBeAdded) ) {
            throw new OstError("wf_ad_evp_1", ErrorCode.INVALID_WORKFLOW_PARAMS);
        }

        hasValidAddress(mDeviceAddressToBeAdded);

        super.ensureValidParams();
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.ADD_DEVICE_WITH_QR;
    }

    static class AddDeviceDataDefinitionInstance implements OstPerform.DataDefinitionInstance {
        private static final String TAG = "AddDeviceDDInstance";
        private final JSONObject dataObject;
        private final String userId;
        private final OstWorkFlowCallback callback;

        public AddDeviceDataDefinitionInstance(JSONObject dataObject, String userId, OstWorkFlowCallback callback) {
            this.dataObject = dataObject;
            this.userId = userId;
            this.callback = callback;
        }

        @Override
        public void validateDataPayload() {
            boolean hasDeviceAddress = dataObject.has(OstConstants.QR_DEVICE_ADDRESS);
            if (!hasDeviceAddress) {
                throw new OstError("wf_pe_pr_2", OstErrors.ErrorCode.INVALID_QR_DEVICE_OPERATION_DATA);
            }
        }

        @Override
        public void validateDataParams() {
        }

        @Override
        public OstContextEntity getContextEntity() {
            JSONObject jsonObject = updateJSONKeys(dataObject);
            OstContextEntity contextEntity = new OstContextEntity(jsonObject, OstSdk.JSON_OBJECT);
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
            OstAddDeviceWithQR ostAddDeviceWithQR = new OstAddDeviceWithQR(userId, deviceAddress, callback);
            ostAddDeviceWithQR.perform();
        }
    }
}