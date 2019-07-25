/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.ostsdkui.walletsetup;

import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;

import ost.com.ostsdkui.BasePresenter;
import ost.com.ostsdkui.OstPassphraseAcceptor;
import ost.com.ostsdkui.sdkInteract.SdkInteract;
import ost.com.ostsdkui.sdkInteract.WorkFlowListener;

class WalletSetUpPresenter extends BasePresenter<SetUpView> implements SdkInteract.RequestAcknowledged {

    private static final String LOG_TAG = "WalletSetUpPresenter";
    private String mFirstPin;
    private int pinCounter;
    private boolean onScreen;
    private boolean requestAcknowledgedActionPending;
    private String userId;
    private long workflowId;
    private long expiredAfterSecs;
    private String spendingLimit;

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

//                if (new CommonUtils().isBioMetricHardwareAvailable() && !new CommonUtils().isBioMetricEnrolled()) {
//                    new CommonUtils().showEnableBiometricDialog(new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            startWorkFLow(pin);
//                        }
//                    });
//                    return;
//                }
                startWorkFLow(pin);
            } else {
                getMvpView().showPinErrorDialog();
            }
        }
    }

    private void startWorkFLow(final String pin) {
        getMvpView().showProgress(true, "Activating user...");
        final WalletSetUpPresenter walletSetUpPresenter = this;
        final WorkFlowListener workFlowListener = SdkInteract.getInstance().getWorkFlowListener(workflowId);
        workFlowListener.getPassphrase(userId,
                new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER),
                new OstPassphraseAcceptor() {
            @Override
            public void setPassphrase(String passphrase) {
                UserPassphrase userPassphrase = new UserPassphrase(userId, pin, passphrase);
                SdkInteract.getInstance().subscribe(workFlowListener.getId(), walletSetUpPresenter);

                OstSdk.activateUser(
                        userPassphrase,
                        expiredAfterSecs,
                        spendingLimit,
                        workFlowListener
                );
            }

            @Override
            public void cancelFlow() {
                getMvpView().showProgress(false);
                getMvpView().showToastMessage("User Activation failed. Please try after sometime.", false);
            }
        });
    }

    @Override
    public void requestAcknowledged(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d(LOG_TAG, "Request Ack for Activate user");
        if (onScreen) {
            getMvpView().showProgress(false);
            (getMvpView()).gotoDashboard(workflowId);
        } else {
            requestAcknowledgedActionPending = true;
        }
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

    public void onResume() {
        onScreen = true;
        if (requestAcknowledgedActionPending) {
            getMvpView().showProgress(false);
            (getMvpView()).gotoDashboard(-1);
        }
    }

    public void onPause() {
        onScreen = false;
    }

    public void setArguments(String userId, long workflowId, long expiredAfterSecs, String spendingLimit) {
        this.userId = userId;
        this.workflowId = workflowId;
        this.expiredAfterSecs = expiredAfterSecs;
        this.spendingLimit = spendingLimit;
    }
}