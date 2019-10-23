/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.workflow.transactions;

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;
import android.util.Log;

import com.ost.walletsdk.OstConfigs;
import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstToken;
import com.ost.walletsdk.network.OstJsonApi;
import com.ost.walletsdk.network.OstJsonApiCallback;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.sdkInteract.SdkInteract;
import com.ost.ostwallet.sdkInteract.WorkFlowListener;
import com.ost.ostwallet.ui.BasePresenter;
import com.ost.ostwallet.util.CommonUtils;

class TransactionsPresenter extends BasePresenter<TransactionsView> implements
        SdkInteract.RequestAcknowledged,
        SdkInteract.FlowInterrupt {

    private static final String LOG_TAG = "OstTransactionPresenter";

    private OstToken mOstToken = OstSdk.getToken(AppProvider.get().getCurrentUser().getTokenId());
    private String mCurrentTokenSymbol = mOstToken.getSymbol();
    private List<String> mUnitList = Arrays.asList(mCurrentTokenSymbol, OstConfigs.getInstance().getPRICE_POINT_CURRENCY_SYMBOL());
    public JSONObject mPricePoint = null;

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
        updateBalance();
    }

    JSONObject sendTokens(String tokenHolderAddress, String tokens, String unit, String userName) {
        getMvpView().showProgress(true, String.format("Sending %s %s to %s", tokens, unit, userName));

        //tokens validation
        //Input token string is in Eth
        BigDecimal tokensBigInt;
        Integer tokenDecimals = Integer.parseInt(mOstToken.getBtDecimals());
        try {
            tokensBigInt = new BigDecimal(tokens);
            if(tokensBigInt.scale() > 2){
                getMvpView().invalidTokenValue("Only 2 digits are allowed after decimal");
                getMvpView().showProgress(false);
                return null;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "tokens value is invalid", e);
            getMvpView().invalidTokenValue("Invalid Token Number");
            getMvpView().showProgress(false);
            return null;
        }

        String transferRule;
        if (mCurrentTokenSymbol.equalsIgnoreCase(unit)) {
            transferRule = OstSdk.RULE_NAME_DIRECT_TRANSFER;

            //Convert tokens to Wei
            BigDecimal tokensInWei = tokensBigInt.multiply( new BigDecimal("10").pow(tokenDecimals)).setScale(0);
            BigDecimal balanceInBigInt = new BigDecimal(AppProvider.get().getCurrentUser().getBalance());
            if (tokensInWei.compareTo(balanceInBigInt) > 0 ) {
                getMvpView().insufficientBalance();
                getMvpView().showProgress(false);
                return  null;
            }
            tokens = tokensInWei.toString();
        } else {
            transferRule = OstSdk.RULE_NAME_PRICER;

            //Provided token are in Cent convert it into Dollar wei
            BigDecimal tokenInDollarWei = tokensBigInt.multiply( new BigDecimal("10").pow(18)).setScale(0);
            String usdBalance = CommonUtils.convertBTWeiToFiat(AppProvider.get().getCurrentUser().getBalance(), mPricePoint);
            if(null == usdBalance){
                usdBalance = "0";
            }
            BigDecimal balanceInDollarWei = new BigDecimal(usdBalance).multiply( new BigDecimal("10").pow(18));
            if (tokenInDollarWei.compareTo(balanceInDollarWei) > 0 ) {
                getMvpView().insufficientBalance();
                getMvpView().showProgress(false);
                return  null;
            }
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

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Tokens sent from Android");
        map.put("type", "user_to_user");
        OstSdk.executeTransaction(
                AppProvider.get().getCurrentUser().getOstUserId(),
                Arrays.asList(tokenHolderAddress),
                Arrays.asList(tokens),
                transferRule,
                map,
                workFlowListener
        );
        return transactionDetails;
    }

    void updateBalance() {
        getMvpView().showProgress(true, "Fetching User Balance");
        OstJsonApi.getBalanceWithPricePoints(AppProvider.get().getCurrentUser().getOstUserId(), new OstJsonApiCallback() {
            @Override
            public void onOstJsonApiSuccess(@Nullable JSONObject jsonObject) {
                if ( null != jsonObject ) {
                    String balance = "0";
                    try{
                        JSONObject balanceData = jsonObject.getJSONObject(jsonObject.getString(OstConstants.RESULT_TYPE));
                        balance = balanceData.getString("available_balance");
                        mPricePoint = jsonObject.optJSONObject("price_point");
                    } catch(Exception e){ }
                    AppProvider.get().getCurrentUser().updateBalance(balance);
                    getMvpView().showProgress(false);
                } else {
                    Log.d(LOG_TAG, "getBalanceWithPricePoints data is null.");
                    getMvpView().showProgress(false);
                }
            }

            @Override
            public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject data) {
                Log.e(LOG_TAG, "getBalanceWithPricePoints InternalErrorCode:" + err.getInternalErrorCode());
                getMvpView().showProgress(false);
            }
        });
    }

    @Override
    public void requestAcknowledged(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        getMvpView().showProgress(false);
        getMvpView().showToastMessage("Transaction received. ", true);
        getMvpView().goToWalletDetails();
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        getMvpView().showProgress(false);
    }
}