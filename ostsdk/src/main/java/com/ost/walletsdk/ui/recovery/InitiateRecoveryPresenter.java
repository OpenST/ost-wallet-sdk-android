/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.recovery;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.ui.sdkInteract.WorkFlowListener;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.workflows.OstWorkflowContext;

import org.json.JSONObject;

class InitiateRecoveryPresenter extends RecoveryPresenter {

    private static final String LOG_TAG = "OstIRPresenter";

    final JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("initiate_recovery");

    private InitiateRecoveryPresenter() {
    }

    public static InitiateRecoveryPresenter getInstance() {
        return new InitiateRecoveryPresenter();
    }

    @Override
    void startWorkFlow(String ostUserId, UserPassphrase currentUserPassPhrase, String deviceAddress, WorkFlowListener workFlowListener) {

        getMvpView().showProgress(true, StringConfig.instance(contentConfig.optJSONObject("loader")).getString());

        OstSdk.initiateDeviceRecovery(
                ostUserId,
                currentUserPassPhrase,
                deviceAddress,
                workFlowListener
        );
    }

    @Override
    protected OstWorkflowContext getWorkFlowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.INITIATE_DEVICE_RECOVERY);
    }
}