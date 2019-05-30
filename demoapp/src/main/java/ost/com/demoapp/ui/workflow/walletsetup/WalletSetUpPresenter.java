/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.walletsetup;

import android.content.DialogInterface;
import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.models.entities.OstToken;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;

import org.json.JSONObject;

import java.math.BigDecimal;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.entity.LogInUser;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BasePresenter;
import ost.com.demoapp.util.CommonUtils;

class WalletSetUpPresenter extends BasePresenter<SetUpView> implements SdkInteract.RequestAcknowledged {

    private static final String LOG_TAG = "WalletSetUpPresenter";
    private String mFirstPin;
    private int pinCounter;

    private WalletSetUpPresenter() {
        pinCounter = 0;
    }

    static WalletSetUpPresenter getInstance() {
        return new WalletSetUpPresenter();
    }

    void onCreateView() {
        (getMvpView()).showAddPin();
    }

    void onPinEntered(String pin) {
        if (0 == pinCounter) {
            mFirstPin = pin;
            (getMvpView()).showRetypePin();
            pinCounter++;
        } else {
            if (mFirstPin.equals(pin)) {
                Log.d(LOG_TAG, "Activate user");

                if (new CommonUtils().isBioMetricHardwareAvailable() && !new CommonUtils().isBioMetricEnrolled()) {
                    new CommonUtils().showEnableBiometricDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startWorkFLow(pin);
                        }
                    });
                    return;
                }
                startWorkFLow(pin);
            } else {
                getMvpView().showPinErrorDialog();
            }
        }
    }

    private void startWorkFLow(String pin) {
        getMvpView().showProgress(true, "Activating user...");
        WalletSetUpPresenter walletSetUpPresenter = this;
        AppProvider.get().getMappyClient().getLoggedInUserPinSalt(new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (new CommonUtils().isValidResponse(jsonObject)){
                    try {
                        JSONObject userSaltObject = (JSONObject) new CommonUtils().parseResponseForResultType(jsonObject);
                        String userPinSalt = userSaltObject.getString("recovery_pin_salt");
                        LogInUser logInUser = AppProvider.get().getCurrentUser();
                        UserPassphrase userPassphrase = new UserPassphrase(logInUser.getOstUserId(), pin, userPinSalt);
                        long expiredAfterInSecs = 30 * 24 * 60 * 60;
                        Integer decimals = Integer.parseInt(OstToken.getById(logInUser.getTokenId()).getBtDecimals());
                        String spendingLimit = new BigDecimal("1000").multiply(new BigDecimal(10).pow(decimals)).toString();
                        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
                        SdkInteract.getInstance().subscribe(workFlowListener.getId(), walletSetUpPresenter);

                        OstSdk.activateUser(
                                userPassphrase,
                                expiredAfterInSecs,
                                spendingLimit,
                                workFlowListener
                        );
                    } catch (Exception e){
                        Log.d("getPinSalt", "Exception in fetching Pin Salt.");
                        getMvpView().showProgress(false);
                        AppProvider.get().getCurrentActivity().showToastMessage("User Activation failed. Please try after sometime.", false);
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("getPinSalt", String.format("Error in fetching Pin Salt. %s", (null != throwable ? throwable.getMessage() : "")));
                getMvpView().showProgress(false);
                AppProvider.get().getCurrentActivity().showToastMessage("User Activation failed. Please try after sometime.", false);
            }
        });
    }

    @Override
    public void requestAcknowledged(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d(LOG_TAG, "Request Ack for Activate user");
        getMvpView().showProgress(false);
        (getMvpView()).gotoDashboard(workflowId);
    }

    void popBack() {
        if (pinCounter > 0) {
            pinCounter--;
        }
    }

    void resetWalletSetUp() {
        pinCounter = 0;
        (getMvpView()).showAddPin();
    }
}