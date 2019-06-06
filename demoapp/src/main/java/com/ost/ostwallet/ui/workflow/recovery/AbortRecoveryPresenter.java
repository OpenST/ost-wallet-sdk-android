/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.workflow.recovery;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.sdkInteract.WorkFlowListener;

class AbortRecoveryPresenter extends RecoveryPresenter {

    private static final String LOG_TAG = "OstARPresenter";

    private AbortRecoveryPresenter() {
    }

    public static AbortRecoveryPresenter getInstance() {
        return new AbortRecoveryPresenter();
    }

    @Override
    void startWorkFlow(String ostUserId, UserPassphrase currentUserPassPhrase, String deviceAddress, WorkFlowListener workFlowListener) {
        getMvpView().showProgress(true, "Aborting recovery...");
        OstSdk.abortDeviceRecovery(
                ostUserId,
                currentUserPassPhrase,
                workFlowListener
        );
    }

    @Override
    void showToast(){
        AppProvider.get().getCurrentActivity().showToastMessage("Request to abort recovery received ", true);
    }
}