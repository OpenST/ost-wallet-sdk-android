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
import com.ost.walletsdk.ui.interfaces.FlowCompleteListener;
import com.ost.walletsdk.ui.interfaces.FlowInterruptListener;
import com.ost.walletsdk.ui.interfaces.RequestAcknowledgedListener;
import com.ost.walletsdk.ui.sdkInteract.WorkFlowListener;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

class EnterMnemonicsPresenter extends BasePresenter<EnterMnemonicsView> implements
        FlowCompleteListener,
        FlowInterruptListener,
        RequestAcknowledgedListener {

    private static final String LOG_TAG = "OstEnterMnemonicsPresenter";
    private OstWorkFlowCallback mWorkFlowListener;
    private String mUserId;

    final JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("add_current_device_with_mnemonics");

    private EnterMnemonicsPresenter() {
    }

    static EnterMnemonicsPresenter getInstance() {
        return new EnterMnemonicsPresenter();
    }

    void recoverWallet(String mnemonicsPhrase) {
        //mnemonics validation
        String[] mnemonicsArray = mnemonicsPhrase.split(" ");
        if (mnemonicsArray.length != 12) {
            getMvpView().showErrorMessage(true);
            return;
        }
        getMvpView().showErrorMessage(false);

        getMvpView().showProgress(true, StringConfig.instance(contentConfig.optJSONObject("initial_loader")).getString());
        getMvpView().onInitLoader(contentConfig);

        OstSdk.authorizeCurrentDeviceWithMnemonics(
                mUserId,
                mnemonicsPhrase.getBytes(),
                mWorkFlowListener
        );
    }

    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        getMvpView().showProgress(false);
    }

    @Override
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        getMvpView().showProgress(false);
    }

    @Override
    public void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {

    }

    public void setArguments(String userId, WorkFlowListener workFlowListener) {
        mUserId = userId;
        mWorkFlowListener = workFlowListener;
    }
}