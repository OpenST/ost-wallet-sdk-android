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

import org.json.JSONObject;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.entity.LogInUser;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BasePresenter;
import ost.com.demoapp.util.CommonUtils;

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
        showToast();
        (getMvpView()).gotoDashboard(workflowId);
    }

    void onPinEntered(String pin) {
        RecoveryPresenter recoveryPresenter = this;
        AppProvider.get().getMappyClient().getLoggedInUserPinSalt(new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (new CommonUtils().isValidResponse(jsonObject)){
                    try {
                        JSONObject userSaltObject = (JSONObject) new CommonUtils().parseResponseForResultType(jsonObject);
                        String userPinSalt = userSaltObject.getString("recovery_pin_salt");
                        LogInUser logInUser = AppProvider.get().getCurrentUser();
                        UserPassphrase currentUserPassPhrase = new UserPassphrase(logInUser.getOstUserId(), pin, userPinSalt);

                        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
                        SdkInteract.getInstance().subscribe(workFlowListener.getId(), recoveryPresenter);

                        startWorkFlow(logInUser.getOstUserId(),
                                currentUserPassPhrase,
                                mDeviceAddress,
                                workFlowListener);
                    } catch (Exception e){
                        Log.d("getPinSalt", "Exception in fetching Pin Salt.");
                        recoverySaltFetchFailed();
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("getPinSalt", String.format("Error in fetching Pin Salt. %s", (null != throwable ? throwable.getMessage() : "")));
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

    public void setDeviceAddress(String deviceAddress) {
        mDeviceAddress = deviceAddress;
    }

    void showToast(){

    }

    private void recoverySaltFetchFailed(){
        getMvpView().showProgress(false);
        getMvpView().gotoDashboard(0);
        AppProvider.get().getCurrentActivity().showToastMessage("Recovery could not be initiated. Please try after sometime.", false);
    }
}