/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.entermnemonics;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ui.BasePresenter;
import com.ost.walletsdk.ui.sdkInteract.SdkInteract;
import com.ost.walletsdk.ui.sdkInteract.WorkFlowListener;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

class EnterMnemonicsPresenter extends BasePresenter<EnterMnemonicsView> implements
        SdkInteract.FlowComplete,
        SdkInteract.FlowInterrupt,
        SdkInteract.RequestAcknowledged {

    private static final String LOG_TAG = "OstEnterMnemonicsPresenter";
    private OstWorkFlowCallback mWorkFlowListener;
    private String mUserId;


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

        OstSdk.authorizeCurrentDeviceWithMnemonics(
                mUserId,
                mnemonicsPhrase.getBytes(),
                mWorkFlowListener
        );
    }

    @Override
    public void flowComplete(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        getMvpView().showProgress(false);
    }

    @Override
    public void flowInterrupt(String workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        getMvpView().showProgress(false);
    }

    @Override
    public void requestAcknowledged(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {

    }

    public void setArguments(String userId, WorkFlowListener workFlowListener) {
        mUserId = userId;
        mWorkFlowListener = workFlowListener;
    }
}