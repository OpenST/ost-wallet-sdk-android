/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.createsession;

import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstToken;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import java.math.BigInteger;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BasePresenter;

class CreateSessionPresenter extends BasePresenter<CreateSessionView> implements
        SdkInteract.FlowInterrupt,
        SdkInteract.RequestAcknowledged {

    private static final String LOG_TAG = "OstCSPresenter";


    private CreateSessionPresenter() {

    }

    static CreateSessionPresenter getInstance() {
        return new CreateSessionPresenter();
    }

    void createSession(String spendingLimit, String unit, String expiryDays) {
        getMvpView().showProgress(true, "Authorizing session...");

        //tokens validation
        //Input spending limit string is in Eth
        BigInteger spendingLimitBigInt;
        try {
            spendingLimitBigInt = new BigInteger(spendingLimit);
        } catch (Exception e) {
            Log.e(LOG_TAG, "spending limit value is invalid", e);
            getMvpView().invalidSpendingLimit();
            getMvpView().showProgress(false);
            return;
        }

        //Convert tokens spending limit to Wei
        Integer decimals = Integer.parseInt(OstToken.getById(AppProvider.get().getCurrentEconomy().getTokenId()).getBtDecimals());
        BigInteger tokensInWei = spendingLimitBigInt.multiply( new BigInteger("10").pow(decimals));
        spendingLimit = tokensInWei.toString();

        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        SdkInteract.getInstance().subscribe(workFlowListener.getId(), this);

        OstSdk.addSession(
                AppProvider.get().getCurrentUser().getOstUserId(),
                spendingLimit,
                Long.parseLong(expiryDays) * 24 * 60 * 60,
                workFlowListener
        );
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        getMvpView().showProgress(false);
    }

    @Override
    public void requestAcknowledged(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        getMvpView().showProgress(false);
        getMvpView().showToastMessage("Session authorization request received.", true);
        getMvpView().goBack();
    }
}