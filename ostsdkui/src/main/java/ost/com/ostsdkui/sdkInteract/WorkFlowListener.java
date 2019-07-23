/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.ostsdkui.sdkInteract;

import android.util.Log;

import com.ost.walletsdk.network.OstApiError;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

import java.util.List;

import ost.com.ostsdkui.OstPassphraseAcceptor;
import ost.com.ostsdkui.OstUserPassphraseCallback;


public class WorkFlowListener implements OstWorkFlowCallback {

    private static final String LOG_TAG = "OstWorkFlowListener";
    private final SdkInteract mSdkInteract;

    private static long identifier = 0;
    private OstUserPassphraseCallback mUserPassphraseCallback;

    public long getId() {
        return mId;
    }

    private final long mId;

    WorkFlowListener() {
        this.mSdkInteract = SdkInteract.getInstance();
        this.mId = identifier++;
    }

    @Override
    public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {
        Log.i(LOG_TAG, String.format("Device Object %s ", apiParams.toString()));
    }

    @Override
    public void getPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        Log.d(LOG_TAG, String.format("Get Pin: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        mSdkInteract.getPinCallbackListener().getPin(getId(), ostWorkflowContext, userId, ostPinAcceptInterface);
    }

    @Override
    public void invalidPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        Log.d(LOG_TAG, String.format("Invalid Pin: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        mSdkInteract.getPinCallbackListener().invalidPin(getId(), ostWorkflowContext, userId, ostPinAcceptInterface);
    }

    @Override
    public void pinValidated(OstWorkflowContext ostWorkflowContext, String userId) {
        Log.d(LOG_TAG, String.format("Pin Validated: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        mSdkInteract.getPinCallbackListener().pinValidated(getId(), ostWorkflowContext, userId);
    }

    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d(LOG_TAG, String.format("Flow Complete: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        mSdkInteract.notifyEvent(getId(), SdkInteract.CALLBACK_TYPE.FLOW_COMPLETE, ostWorkflowContext, ostContextEntity);
        mSdkInteract.unRegister(this);
    }

    @Override
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        Log.d(LOG_TAG, String.format("Flow Interrupted: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        mSdkInteract.notifyEvent(getId(), SdkInteract.CALLBACK_TYPE.FLOW_INTERRUPT, ostWorkflowContext, ostError);
        mSdkInteract.unRegister(this);
    }

    @Override
    public void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d(LOG_TAG, String.format("Request Acknowledged: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));


        mSdkInteract.notifyEvent(getId(), SdkInteract.CALLBACK_TYPE.REQUEST_ACK, ostWorkflowContext, ostContextEntity);
    }

    @Override
    public void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {
        Log.d(LOG_TAG, String.format("Verify Data: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        mSdkInteract.getVerifyDataCallbackListener().verifyData(getId(), ostWorkflowContext, ostContextEntity, ostVerifyDataInterface);
    }


    private void logSdkError(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        StringBuilder errorStringBuilder = new StringBuilder();

        String errorString = String.format("Error: %s " +
                        "\nwith error code: %s" +
                        "\ninternal error code: %s",
                ostError.getMessage(),
                ostError.getErrorCode(),
                ostError.getInternalErrorCode()
        );
        errorStringBuilder.append(errorString);

        if (ostError.isApiError()) {
            OstApiError ostApiError = ((OstApiError) ostError);
            String apiErrorCodeMsg = String.format(
                    "\n%s: %s",
                    ostApiError.getErrCode(),
                    ostApiError.getErrMsg());

            errorStringBuilder.append(apiErrorCodeMsg);

            errorStringBuilder.append(
                    String.format("\napi_internal_id: %s", ostApiError.getApiInternalId())
            );

            List<OstApiError.ApiErrorData> apiErrorDataList = ostApiError.getErrorData();
            for (OstApiError.ApiErrorData apiErrorData : apiErrorDataList) {
                String errorData = String.format(
                        "\n%s: %s",
                        apiErrorData.getParameter(),
                        apiErrorData.getMsg());

                errorStringBuilder.append(errorData);
            }
        }
        String stringFormattedError = errorStringBuilder.toString();
        Log.e(LOG_TAG, stringFormattedError);

    }

    public void setUserPassPhraseCallback(OstUserPassphraseCallback userPassphraseCallback) {
        this.mUserPassphraseCallback = userPassphraseCallback;
    }

    public void getPassphrase(String userId, OstPassphraseAcceptor ostPassphraseAcceptor) {
        if (null == mUserPassphraseCallback) {
            ostPassphraseAcceptor.cancelFlow();
        } else {
            this.mUserPassphraseCallback.getPassphrase(userId, ostPassphraseAcceptor);
        }
    }
}