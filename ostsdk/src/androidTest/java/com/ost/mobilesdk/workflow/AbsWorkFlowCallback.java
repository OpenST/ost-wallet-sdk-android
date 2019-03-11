package com.ost.mobilesdk.workflow;

import com.ost.mobilesdk.workflows.OstContextEntity;
import com.ost.mobilesdk.workflows.OstWorkflowContext;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

public class AbsWorkFlowCallback implements OstWorkFlowCallback {
    @Override
    public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {

    }

    @Override
    public void getPin(OstWorkflowContext workflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {

    }

    @Override
    public void invalidPin(OstWorkflowContext workflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {

    }

    @Override
    public void pinValidated(OstWorkflowContext workflowContext, String userId) {

    }

    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {

    }

    @Override
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {

    }

    @Override
    public void deviceUnauthorized() {

    }

    @Override
    public void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {

    }

    @Override
    public void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {

    }
}
