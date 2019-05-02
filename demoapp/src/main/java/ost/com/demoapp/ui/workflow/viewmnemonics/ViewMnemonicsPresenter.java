/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.viewmnemonics;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BasePresenter;

class ViewMnemonicsPresenter extends BasePresenter<ViewMnemonicsView> implements
        SdkInteract.FlowComplete,
        SdkInteract.FlowInterrupt {

    private static final String LOG_TAG = "ViewMnemonicsPresenter";


    private ViewMnemonicsPresenter() {
    }

    static ViewMnemonicsPresenter getInstance() {
        return new ViewMnemonicsPresenter();
    }

    @Override
    public void attachView(ViewMnemonicsView mvpView) {
        super.attachView(mvpView);
        getMvpView().showProgress(true);
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        SdkInteract.getInstance().subscribe(workFlowListener.getId(), this);
        OstSdk.getDeviceMnemonics(AppProvider.get().getCurrentUser().getOstUserId(), workFlowListener);
    }

    @Override
    public void flowComplete(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        getMvpView().showProgress(false);
        if (OstWorkflowContext.WORKFLOW_TYPE.GET_DEVICE_MNEMONICS.equals(ostWorkflowContext.getWorkflow_type())) {
            if (OstSdk.MNEMONICS.equals(ostContextEntity.getEntityType())) {
                byte[] mnemonics = (byte[]) ostContextEntity.getEntity();
                getMvpView().showMnemonics(new String(mnemonics));
            }
        }
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        getMvpView().showProgress(false);
    }
}