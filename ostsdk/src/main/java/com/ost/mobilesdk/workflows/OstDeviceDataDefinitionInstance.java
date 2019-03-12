package com.ost.mobilesdk.workflows;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

class OstDeviceDataDefinitionInstance implements OstPerform.DataDefinitionInstance {
    private static final String TAG = "DeviceDDInstance";
    final JSONObject dataObject;
    final String userId;
    final OstWorkFlowCallback callback;

    public OstDeviceDataDefinitionInstance(JSONObject dataObject, String userId, OstWorkFlowCallback callback) {
        this.dataObject = dataObject;
        this.userId = userId;
        this.callback = callback;
    }

    String getDeviceAddress() {
        return dataObject.optString(OstConstants.QR_DEVICE_ADDRESS);
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
        String deviceAddress = dataObject.optString(OstConstants.QR_DEVICE_ADDRESS);
        OstDevice ostDevice = OstDevice.getById(deviceAddress);
        OstContextEntity contextEntity = new OstContextEntity(ostDevice, OstSdk.DEVICE);
        return contextEntity;
    }

    @Override
    public void startDataDefinitionFlow() {

    }

    @Override
    public void validateApiDependentParams() {

    }
}