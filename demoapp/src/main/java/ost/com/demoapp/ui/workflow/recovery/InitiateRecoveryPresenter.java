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

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;

import ost.com.demoapp.sdkInteract.WorkFlowListener;

class InitiateRecoveryPresenter extends RecoveryPresenter {

    private static final String LOG_TAG = "OstIRPresenter";

    private InitiateRecoveryPresenter() {
    }

    public static InitiateRecoveryPresenter getInstance() {
        return new InitiateRecoveryPresenter();
    }

    @Override
    void startWorkFlow(String ostUserId, UserPassphrase currentUserPassPhrase, String deviceAddress, WorkFlowListener workFlowListener) {
        OstSdk.initiateDeviceRecovery(
                ostUserId,
                currentUserPassPhrase,
                deviceAddress,
                workFlowListener
        );
    }
}