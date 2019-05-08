/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.authrorizedeviceqr;

import android.content.Intent;
import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONException;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BasePresenter;

class AuthorizeDeviceQRPresenter extends BasePresenter<AuthorizeDeviceQRView> implements
        SdkInteract.RequestAcknowledged,
        SdkInteract.FlowInterrupt {

    private static final String LOG_TAG = "OstADQRPresenter";


    private AuthorizeDeviceQRPresenter() {
    }

    static AuthorizeDeviceQRPresenter getInstance() {
        return new AuthorizeDeviceQRPresenter();
    }


    public void onCreateView() {
        getMvpView().launchQRScanner();
    }


    @Override
    public void requestAcknowledged(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d(LOG_TAG, "Request Ack for Activate user");
        getMvpView().showProgress(false);
        (getMvpView()).gotoDashboard(workflowId);
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        Log.e(LOG_TAG, "Flow Interrupt");
        getMvpView().showProgress(false);
    }

    void processQRResult(Intent data) {
        Log.d(LOG_TAG, String.format("QR process result %s", data));
        getMvpView().goBack();
        getMvpView().showProgress(true, "Authorizing device...");
        if (data != null && data.getData() != null) {
            String returnedResult = data.getData().toString();
            WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
            SdkInteract.getInstance().subscribe(workFlowListener.getId(), this);

            try {
                OstSdk.performQRAction(
                        AppProvider.get().getCurrentUser().getOstUserId(),
                        returnedResult,
                        workFlowListener
                );
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Exception in Data;");
                getMvpView().showProgress(false);
            }
        }
    }
}