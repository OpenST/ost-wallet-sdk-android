/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.resetpin;

import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.ui.BasePresenter;
import com.ost.walletsdk.ui.OstPassphraseAcceptor;
import com.ost.walletsdk.ui.sdkInteract.SdkInteract;
import com.ost.walletsdk.ui.sdkInteract.WorkFlowListener;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import org.json.JSONObject;

class ResetPinPresenter extends BasePresenter<ResetPinView> {

    private static final String LOG_TAG = "OstResetPinPresenter";
    private int pinCounter;
    private String mCurrentPin;
    private String mFirstNewPin;
    private String mUserId;
    private String mWorkflowId;

    private final JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("reset_pin");

    private ResetPinPresenter() {
        pinCounter = 0;
    }

    static ResetPinPresenter getInstance() {
        return new ResetPinPresenter();
    }


    public void onCreateView() {
        getMvpView().showEnterCurrentPin();
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
                getMvpView().showProgress(true, StringConfig.instance(contentConfig.optJSONObject("loader")).getString());
                getMvpView().onPostAuthentication();

                final WorkFlowListener workFlowListener = SdkInteract.getInstance().getWorkFlowListener(mWorkflowId);
                workFlowListener.getPassphrase(mUserId, new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.RESET_PIN), new OstPassphraseAcceptor() {
                    @Override
                    public void setPassphrase(String passphrase) {
                        UserPassphrase currentUserPassPhrase = new UserPassphrase(mUserId, mCurrentPin, passphrase);
                        UserPassphrase newUserPassPhrase = new UserPassphrase(mUserId, mFirstNewPin, passphrase);

                        OstSdk.resetPin(mUserId, currentUserPassPhrase, newUserPassPhrase, workFlowListener);
                    }

                    @Override
                    public void cancelFlow() {
                        Log.d("getPinSalt", "Exception in fetching Pin Salt.");
                        getMvpView().showProgress(false);
                        OstError error = new OstError("rpp_ope_cf", OstErrors.ErrorCode.WORKFLOW_CANCELLED);
                        workFlowListener.flowInterrupt(new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.RESET_PIN), error);
                    }
                });

            } else {
                Log.d(LOG_TAG,"Retype Pin is not equal");
                getMvpView().showPinErrorDialog();
            }
        }
    }

    void resetResetPin() {
        pinCounter = 1;
        (getMvpView()).showSetNewPin();
    }

    public void setArguments(String userId, String workflowId) {
        mUserId = userId;
        mWorkflowId = workflowId;
    }

    void goBackChildFragment() {
        pinCounter--;
        if(pinCounter == 1) {
            getMvpView().showSetNewPin();
        } else {
            getMvpView().showEnterCurrentPin();
        }
    }

    boolean haveBackStackFragment() {
        return pinCounter > 0;
    }
}