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

import com.ost.walletsdk.OstConfigs;
import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstToken;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BasePresenter;
import ost.com.demoapp.util.CommonUtils;

class TransactionsPresenter extends BasePresenter<TransactionsView> implements
        SdkInteract.RequestAcknowledged,
        SdkInteract.FlowInterrupt {

    private static final String LOG_TAG = "OstTransactionPresenter";

    private OstToken mOstToken = OstSdk.getToken(AppProvider.get().getCurrentUser().getTokenId());
    private String mCurrentTokenSymbol = mOstToken.getSymbol();
    private List<String> mUnitList = Arrays.asList(mCurrentTokenSymbol, OstConfigs.getInstance().PRICE_POINT_CURRENCY_SYMBOL);
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

    JSONObject sendTokens(String tokenHolderAddress, String tokens, String unit) {
        getMvpView().showProgress(true, "Transaction processing...");

        //tokens validation
        //Input token string is in Eth
        BigDecimal tokensBigInt;
        try {
            tokensBigInt = new BigDecimal(tokens);
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
            Integer decimals = Integer.parseInt(mOstToken.getBtDecimals());
            BigDecimal tokensInWei = tokensBigInt.multiply( new BigDecimal("10").pow(decimals)).setScale(0);
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
            String usdBalance = CommonUtils.convertBTWeiToUsd(AppProvider.get().getCurrentUser().getBalance(), mPricePoint);
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
        AppProvider.get().getMappyClient().getCurrentUserBalance(new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                String balance = "0";
                if (new CommonUtils().isValidResponse(jsonObject)) {
                    balance = new CommonUtils().parseStringResponseForKey(jsonObject, "available_balance");
                    try{
                        JSONObject jsonData = jsonObject.getJSONObject(OstConstants.RESPONSE_DATA);
                        mPricePoint = jsonData.optJSONObject("price_point");
                    } catch(Exception e){ }
                }
                AppProvider.get().getCurrentUser().updateBalance(balance);
                getMvpView().showProgress(false);
            }

            @Override
            public void onFailure(Throwable throwable) {
                getMvpView().showProgress(false);
            }
        });
    }

    @Override
    public void requestAcknowledged(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        getMvpView().showProgress(false);
        getMvpView().showToastMessage("Transaction received. ", true);
        getMvpView().goBack();
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        getMvpView().showProgress(false);
    }
}