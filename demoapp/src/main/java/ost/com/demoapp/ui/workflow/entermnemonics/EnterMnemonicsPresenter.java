/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.entermnemonics;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BasePresenter;

class EnterMnemonicsPresenter extends BasePresenter<EnterMnemonicsView> implements
        SdkInteract.FlowComplete,
        SdkInteract.FlowInterrupt,
        SdkInteract.RequestAcknowledged {

    private static final String LOG_TAG = "OstEnterMnemonicsPresenter";


    private EnterMnemonicsPresenter() {
    }

    static EnterMnemonicsPresenter getInstance() {
        return new EnterMnemonicsPresenter();
    }

    void recoverWallet(String mnemonicsPhrase) {
        //mnemonics validation
        String[] mnemonicsArray = mnemonicsPhrase.split(" ");
        if (mnemonicsArray.length != 12) {
            getMvpView().showToastMessage("Mnemonics length should be of 12 words", false);
            return;
        }

        getMvpView().showProgress(true, "Authorizing...");

        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        SdkInteract.getInstance().subscribe(workFlowListener.getId(), this);

        OstSdk.authorizeCurrentDeviceWithMnemonics(
                AppProvider.get().getCurrentUser().getOstUserId(),
                mnemonicsPhrase.getBytes(),
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

    }
}