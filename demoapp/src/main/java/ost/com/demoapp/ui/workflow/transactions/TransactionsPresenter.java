/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.transactions;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BasePresenter;

class TransactionsPresenter extends BasePresenter<TransactionsView> implements
        SdkInteract.RequestAcknowledged,
        SdkInteract.FlowInterrupt {

    private static final String LOG_TAG = "OstTransactionsPresenter";
    private static final String DIRECT_TRANSFER = "DT";

    private String mCurrentTokenSymbol = AppProvider.get().getCurrentEconomy().getTokenSymbol();
    private List<String> mUnitList = Arrays.asList(mCurrentTokenSymbol, "USD");

    public List<String> getUnitList() {
        return mUnitList;
    }

    private TransactionsPresenter() {
    }

    static TransactionsPresenter getInstance() {
        return new TransactionsPresenter();
    }

    @Override
    public void attachView(TransactionsView mvpView) {
        super.attachView(mvpView);
    }

    JSONObject sendTokens(String tokenHolderAddress, String tokens, String unit) {
        getMvpView().showProgress(true, "Transaction in progress...");

        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        SdkInteract.getInstance().subscribe(workFlowListener.getId(), this);
        String transferRule = mCurrentTokenSymbol.equalsIgnoreCase(unit) ? OstSdk.RULE_NAME_DIRECT_TRANSFER : OstSdk.RULE_NAME_PRICER;
        JSONObject transactionDetails = new JSONObject();

        try{
            transactionDetails.put("workflowId", workFlowListener.getId());
            transactionDetails.put("amount", tokens);
            transactionDetails.put("transferRule", transferRule);
        } catch (JSONException e){

        }

        OstSdk.executeTransaction(
                AppProvider.get().getCurrentUser().getOstUserId(),
                Arrays.asList(tokenHolderAddress),
                Arrays.asList(tokens),
                transferRule,
                workFlowListener
        );
        return transactionDetails;
    }

    @Override
    public void requestAcknowledged(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        getMvpView().showProgress(false);
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        getMvpView().showProgress(false);
    }
}