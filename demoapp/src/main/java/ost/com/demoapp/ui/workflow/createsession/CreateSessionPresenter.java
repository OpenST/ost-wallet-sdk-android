/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.createsession;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BasePresenter;

class CreateSessionPresenter extends BasePresenter<CreateSessionView> implements
        SdkInteract.FlowComplete,
        SdkInteract.FlowInterrupt,
        SdkInteract.RequestAcknowledged {

    private static final String LOG_TAG = "OstCreateSessionPresenter";


    private CreateSessionPresenter() {

    }

    static CreateSessionPresenter getInstance() {
        return new CreateSessionPresenter();
    }

    void createSession(String spendingLimit, String unit, String expiryDays) {
        getMvpView().showProgress(true);

        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        SdkInteract.getInstance().subscribe(workFlowListener.getId(), this);

        OstSdk.addSession(
                AppProvider.get().getCurrentUser().getOstUserId(),
                spendingLimit,
                Long.parseLong(expiryDays) * 24 * 60 * 60,
                workFlowListener
        );
    }

    @Override
    public void flowComplete(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        getMvpView().showProgress(false);
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        getMvpView().showProgress(false);
    }

    @Override
    public void requestAcknowledged(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        getMvpView().showProgress(false);
    }
}