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

import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BasePresenter;

class TransactionsPresenter extends BasePresenter<TransactionsView> implements
        SdkInteract.RequestAcknowledged,
        SdkInteract.FlowInterrupt {

    private static final String LOG_TAG = "OstTransactionPresenter";

    private String mCurrentTokenSymbol = AppProvider.get().getCurrentEconomy().getTokenSymbol();
    private List<String> mUnitList = Arrays.asList(mCurrentTokenSymbol);

    public List<String> getUnitList() {
        return mUnitList;
    }

    private TransactionsPresenter() {
        mUnitList.add(OstSdk.getToken(AppProvider.get().getCurrentUser().getTokenId()).getCurrencySymbol());
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

        //tokens validation
        //Input token string is in Eth
        BigInteger tokensBigInt;
        try {
            tokensBigInt = new BigInteger(tokens);
        } catch (Exception e) {
            Log.e(LOG_TAG, "tokens value is invalid", e);
            getMvpView().invalidTokenValue();
            getMvpView().showProgress(false);
            return null;
        }

        String transferRule;
        if (mCurrentTokenSymbol.equalsIgnoreCase(unit)) {
            transferRule = OstSdk.RULE_NAME_DIRECT_TRANSFER;

            //Convert tokens to Wei
            BigInteger tokensInWei = tokensBigInt.multiply( new BigInteger("10").pow(18));
            tokens = tokensInWei.toString();
            BigInteger balanceInBigInt = new BigInteger(AppProvider.get().getCurrentUser().getBalance());
            if (tokensInWei.compareTo(balanceInBigInt) > 0 ) {
                getMvpView().insufficientBalance();
                getMvpView().showProgress(false);
                return  null;
            }
        } else {
            transferRule = OstSdk.RULE_NAME_PRICER;

            //Provided token are in Cent convert it into Dollar wei
            BigInteger tokenInDollarWei = tokensBigInt.multiply( new BigInteger("10").pow(16));
            tokens = tokenInDollarWei.toString();
        }


        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        JSONObject transactionDetails = new JSONObject();
        try{
            transactionDetails.put("workflowId", workFlowListener.getId());
            transactionDetails.put("amount", tokens);
            transactionDetails.put("transferRule", transferRule);
        } catch (JSONException e){
            Log.e(LOG_TAG, "Invalid Transaction details", e);
        }

        SdkInteract.getInstance().subscribe(workFlowListener.getId(), this);

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
        getMvpView().showToastMessage("Your transaction has been broadcasted", true);
        getMvpView().goBack();
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        getMvpView().showProgress(false);
    }
}