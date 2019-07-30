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
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

class RecoveryPresenter extends BasePresenter<RecoveryView> implements
        SdkInteract.RequestAcknowledged,
        SdkInteract.FlowInterrupt {

    private static final String LOG_TAG = "OstRecoveryPresenter";
    private String mDeviceAddress;
    private String mWorkflowId;
    private String mUserId;

    RecoveryPresenter() {
    }


    public void onCreateView() {
        getMvpView().showEnterPin();
    }


    @Override
    public void requestAcknowledged(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d(LOG_TAG, "Request Ack for recovery");
        getMvpView().showProgress(false);
        showToast();
        (getMvpView()).gotoDashboard(workflowId);
    }

    void onPinEntered(final String pin) {
        final RecoveryPresenter recoveryPresenter = this;
        final WorkFlowListener workFlowListener = SdkInteract.getInstance().getWorkFlowListener(mWorkflowId);
        workFlowListener.getPassphrase(mUserId, getWorkFlowContext() ,new OstPassphraseAcceptor() {
            @Override
            public void setPassphrase(String passphrase) {
                UserPassphrase userPassphrase = new UserPassphrase(mUserId, pin, passphrase);
                SdkInteract.getInstance().subscribe(workFlowListener.getId(), recoveryPresenter);
                startWorkFlow(mUserId, userPassphrase ,mDeviceAddress, workFlowListener);
            }

            @Override
            public void cancelFlow() {
                Log.d("getPinSalt", "Exception in fetching Pin Salt.");
                recoverySaltFetchFailed();
            }
        });
    }

    protected OstWorkflowContext getWorkFlowContext() {
        return new OstWorkflowContext();
    }

    void startWorkFlow(String ostUserId, UserPassphrase currentUserPassPhrase, String mDeviceAddress, WorkFlowListener workFlowListener) {

    }

    @Override
    public void flowInterrupt(String workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        getMvpView().showProgress(false);
    }

    void showToast(){

    }

    private void recoverySaltFetchFailed(){
        getMvpView().showProgress(false);
        getMvpView().gotoDashboard(null);
        getMvpView().showToastMessage("Recovery could not be initiated. Please try after sometime.", false);
    }

    public void setArguments(String userId, String workflowId, String deviceAddress) {
        this.mUserId = userId;
        this.mWorkflowId = workflowId;
        this.mDeviceAddress = deviceAddress;
    }
}