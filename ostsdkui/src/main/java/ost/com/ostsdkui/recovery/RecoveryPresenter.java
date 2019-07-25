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

import android.util.Log;

import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import ost.com.ostsdkui.BasePresenter;
import ost.com.ostsdkui.OstPassphraseAcceptor;
import ost.com.ostsdkui.sdkInteract.SdkInteract;
import ost.com.ostsdkui.sdkInteract.WorkFlowListener;

class RecoveryPresenter extends BasePresenter<RecoveryView> implements
        SdkInteract.RequestAcknowledged,
        SdkInteract.FlowInterrupt {

    private static final String LOG_TAG = "OstRecoveryPresenter";
    private String mDeviceAddress;
    private long mWorkflowId;
    private String mUserId;

    RecoveryPresenter() {
    }


    public void onCreateView() {
        getMvpView().showEnterPin();
    }


    @Override
    public void requestAcknowledged(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d(LOG_TAG, "Request Ack for recovery");
        getMvpView().showProgress(false);
        showToast();
        (getMvpView()).gotoDashboard(workflowId);
    }

    void onPinEntered(final String pin) {
        final RecoveryPresenter recoveryPresenter = this;
        final WorkFlowListener workFlowListener = SdkInteract.getInstance().getWorkFlowListener(mWorkflowId);
        workFlowListener.getPassphrase(mUserId, new OstPassphraseAcceptor() {
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

    void startWorkFlow(String ostUserId, UserPassphrase currentUserPassPhrase, String mDeviceAddress, WorkFlowListener workFlowListener) {

    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        getMvpView().showProgress(false);
    }

    void showToast(){

    }

    private void recoverySaltFetchFailed(){
        getMvpView().showProgress(false);
        getMvpView().gotoDashboard(0);
        getMvpView().showToastMessage("Recovery could not be initiated. Please try after sometime.", false);
    }

    public void setArguments(String userId, long workflowId, String deviceAddress) {
        this.mUserId = userId;
        this.mWorkflowId = workflowId;
        this.mDeviceAddress = deviceAddress;
    }
}