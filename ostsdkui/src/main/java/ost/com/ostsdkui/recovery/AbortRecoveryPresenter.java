/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.ostsdkui.recovery;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.workflows.OstWorkflowContext;

import ost.com.ostsdkui.sdkInteract.WorkFlowListener;

class AbortRecoveryPresenter extends RecoveryPresenter {

    private static final String LOG_TAG = "OstARPresenter";

    private AbortRecoveryPresenter() {
    }

    public static AbortRecoveryPresenter getInstance() {
        return new AbortRecoveryPresenter();
    }

    @Override
    void startWorkFlow(String ostUserId, UserPassphrase currentUserPassPhrase, String deviceAddress, WorkFlowListener workFlowListener) {
        getMvpView().showProgress(true, "Aborting recovery...");
        OstSdk.abortDeviceRecovery(
                ostUserId,
                currentUserPassPhrase,
                workFlowListener
        );
    }

    @Override
    void showToast(){
        getMvpView().showToastMessage("Request to abort recovery received ", true);
    }

    @Override
    protected OstWorkflowContext getWorkFlowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.ABORT_DEVICE_RECOVERY);
    }
}