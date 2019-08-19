/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.workflow.viewmnemonics;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.sdkInteract.SdkInteract;
import com.ost.ostwallet.sdkInteract.WorkFlowListener;
import com.ost.ostwallet.ui.BasePresenter;

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
        getMvpView().showProgress(true, "Getting device mnemonics...");
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        SdkInteract.getInstance().subscribe(workFlowListener.getId(), this);
        OstSdk.getDeviceMnemonics(AppProvider.get().getCurrentUser().getOstUserId(), workFlowListener);
    }

    @Override
    public void flowComplete(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        getMvpView().showProgress(false);
        if (OstWorkflowContext.WORKFLOW_TYPE.GET_DEVICE_MNEMONICS.equals(ostWorkflowContext.getWorkflowType())) {
            if (OstSdk.MNEMONICS.equals(ostContextEntity.getEntityType())) {
                byte[] mnemonics = (byte[]) ostContextEntity.getEntity();
                getMvpView().showMnemonics(new String(mnemonics));
            }
        }
    }

    @Override
    public void flowInterrupt(String workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        getMvpView().showProgress(false);
        if (OstWorkflowContext.WORKFLOW_TYPE.GET_DEVICE_MNEMONICS.equals(ostWorkflowContext.getWorkflowType())) {
            getMvpView().showError(ostError.getMessage());
        }
    }
}