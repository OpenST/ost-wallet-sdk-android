/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.recovery;

import android.util.Log;

import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.entity.LogInUser;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BasePresenter;

class RecoveryPresenter extends BasePresenter<RecoveryView> implements
        SdkInteract.RequestAcknowledged,
        SdkInteract.FlowInterrupt {

    private static final String LOG_TAG = "OstRecoveryPresenter";
    private String mDeviceAddress;

    RecoveryPresenter() {
    }


    public void onCreateView() {
        getMvpView().showEnterPin();
    }


    @Override
    public void requestAcknowledged(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d(LOG_TAG, "Request Ack for recovery");
        getMvpView().showProgress(false);
        (getMvpView()).gotoDashboard(workflowId);
    }

    void onPinEntered(String pin) {
        LogInUser logInUser = AppProvider.get().getCurrentUser();
        UserPassphrase currentUserPassPhrase = new UserPassphrase(logInUser.getOstUserId(), pin, logInUser.getUserPinSalt());

        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        SdkInteract.getInstance().subscribe(workFlowListener.getId(), this);

        startWorkFlow(logInUser.getOstUserId(),
                currentUserPassPhrase,
                mDeviceAddress,
                workFlowListener);
    }

    void startWorkFlow(String ostUserId, UserPassphrase currentUserPassPhrase, String mDeviceAddress, WorkFlowListener workFlowListener) {

    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        getMvpView().showProgress(false);
    }

    public void setDeviceAddress(String deviceAddress) {
        mDeviceAddress = deviceAddress;
    }
}