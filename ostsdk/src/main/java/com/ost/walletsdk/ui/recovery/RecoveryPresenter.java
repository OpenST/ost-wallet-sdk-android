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

import android.util.Log;

import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.ui.BasePresenter;
import com.ost.walletsdk.ui.OstPassphraseAcceptor;
import com.ost.walletsdk.ui.sdkInteract.SdkInteract;
import com.ost.walletsdk.ui.sdkInteract.WorkFlowListener;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

class RecoveryPresenter extends BasePresenter<RecoveryView> {

    private static final String LOG_TAG = "OstRecoveryPresenter";
    private String mDeviceAddress;
    private String mWorkflowId;
    private String mUserId;

    RecoveryPresenter() {
    }


    public void onCreateView() {
        getMvpView().showEnterPin();
    }

    void onPinEntered(final String pin) {
        final WorkFlowListener workFlowListener = SdkInteract.getInstance().getWorkFlowListener(mWorkflowId);
        workFlowListener.getPassphrase(mUserId, getWorkFlowContext() ,new OstPassphraseAcceptor() {
            @Override
            public void setPassphrase(String passphrase) {
                UserPassphrase userPassphrase = new UserPassphrase(mUserId, pin, passphrase);
                startWorkFlow(mUserId, userPassphrase ,mDeviceAddress, workFlowListener);
            }

            @Override
            public void cancelFlow() {
                Log.d("getPinSalt", "Exception in fetching Pin Salt.");
                getMvpView().showProgress(false);
                OstError error = new OstError("ws_wsp_cf", OstErrors.ErrorCode.WORKFLOW_CANCELLED);
                workFlowListener.flowInterrupt(getWorkFlowContext(), error);
            }
        });
    }

    protected OstWorkflowContext getWorkFlowContext() {
        return new OstWorkflowContext();
    }

    void startWorkFlow(String ostUserId, UserPassphrase currentUserPassPhrase, String mDeviceAddress, WorkFlowListener workFlowListener) {

    }

    public void setArguments(String userId, String workflowId, String deviceAddress) {
        this.mUserId = userId;
        this.mWorkflowId = workflowId;
        this.mDeviceAddress = deviceAddress;
    }
}