/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.workflow.resetpin;

import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONObject;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.entity.LogInUser;
import com.ost.ostwallet.network.MappyNetworkClient;
import com.ost.ostwallet.sdkInteract.SdkInteract;
import com.ost.ostwallet.sdkInteract.WorkFlowListener;
import com.ost.ostwallet.ui.BasePresenter;
import com.ost.ostwallet.util.CommonUtils;

class ResetPinPresenter extends BasePresenter<ResetPinView> implements
        SdkInteract.RequestAcknowledged,
        SdkInteract.FlowInterrupt {

    private static final String LOG_TAG = "OstResetPinPresenter";
    private int pinCounter;
    private String mCurrentPin;
    private String mFirstNewPin;


    private ResetPinPresenter() {
        pinCounter = 0;
    }

    static ResetPinPresenter getInstance() {
        return new ResetPinPresenter();
    }


    public void onCreateView() {
        getMvpView().showEnterCurrentPin();
    }


    @Override
    public void requestAcknowledged(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d(LOG_TAG, "Request Ack for Activate user");
        getMvpView().showProgress(false);
        (getMvpView()).gotoDashboard(workflowId);
        AppProvider.get().getCurrentActivity().showToastMessage("Reset request received. This request may take up to 60 seconds to process.", true);
    }

    public void onPinEntered(String pin) {
        if (0 == pinCounter) {
            Log.d(LOG_TAG,"Current pin is entered");

            mCurrentPin = pin;
            (getMvpView()).showSetNewPin();
            pinCounter++;
        } else if (1 == pinCounter){
            Log.d(LOG_TAG,"First pin entered");

            mFirstNewPin = pin;
            getMvpView().showRetypePin();
            pinCounter++;
        } else {
            if (mFirstNewPin.equals(pin)) {
                Log.d(LOG_TAG,"Retyped Pin is equal");
                ResetPinPresenter resetPinPresenter = this;
                getMvpView().showProgress(true, "Resetting PIN...");
                AppProvider.get().getMappyClient().getLoggedInUserPinSalt(new MappyNetworkClient.ResponseCallback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        if (new CommonUtils().isValidResponse(jsonObject)){
                            try {
                                JSONObject userSaltObject = (JSONObject) new CommonUtils().parseResponseForResultType(jsonObject);
                                String userPinSalt = userSaltObject.getString("recovery_pin_salt");
                                LogInUser logInUser = AppProvider.get().getCurrentUser();
                                UserPassphrase currentUserPassPhrase = new UserPassphrase(logInUser.getOstUserId(), mCurrentPin, userPinSalt);
                                UserPassphrase newUserPassPhrase = new UserPassphrase(logInUser.getOstUserId(), mFirstNewPin, userPinSalt);

                                WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
                                SdkInteract.getInstance().subscribe(workFlowListener.getId(), resetPinPresenter);

                                OstSdk.resetPin(
                                        logInUser.getOstUserId(),
                                        currentUserPassPhrase,
                                        newUserPassPhrase,
                                        workFlowListener
                                );
                            } catch (Exception e){
                                Log.d("getPinSalt", "Exception in fetching Pin Salt.");
                                getMvpView().showProgress(false);
                                AppProvider.get().getCurrentActivity().showToastMessage("Reset PIN failed. Please try after sometime.", false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.d("getPinSalt", String.format("Error in fetching Pin Salt. %s", (null != throwable ? throwable.getMessage() : "")));
                        getMvpView().showProgress(false);
                        AppProvider.get().getCurrentActivity().showToastMessage("Reset PIN failed. Please try after sometime.", false);
                    }
                });
            } else {
                Log.d(LOG_TAG,"Retype Pin is not equal");
                getMvpView().showPinErrorDialog();
            }
        }
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {

    }

    void resetResetPin() {
        pinCounter = 1;
        (getMvpView()).showSetNewPin();
    }
}