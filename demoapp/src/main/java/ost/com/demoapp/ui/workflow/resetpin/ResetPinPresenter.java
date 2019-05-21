/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.resetpin;

import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.entity.LogInUser;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BasePresenter;

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

                LogInUser logInUser = AppProvider.get().getCurrentUser();
                UserPassphrase currentUserPassPhrase = new UserPassphrase(logInUser.getOstUserId(), mCurrentPin, logInUser.getUserPinSalt());
                UserPassphrase newUserPassPhrase = new UserPassphrase(logInUser.getOstUserId(), mFirstNewPin, logInUser.getUserPinSalt());

                WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
                SdkInteract.getInstance().subscribe(workFlowListener.getId(), this);

                getMvpView().showProgress(true, "Reset pin in progress...");

                OstSdk.resetPin(
                        logInUser.getOstUserId(),
                        currentUserPassPhrase,
                        newUserPassPhrase,
                        workFlowListener
                );
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