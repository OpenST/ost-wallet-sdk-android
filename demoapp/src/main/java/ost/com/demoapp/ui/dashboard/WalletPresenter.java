/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.dashboard;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.network.OstJsonApi;
import com.ost.walletsdk.network.OstJsonApiCallback;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.entity.Transaction;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.ui.BasePresenter;
import ost.com.demoapp.util.CommonUtils;

class WalletPresenter extends BasePresenter<WalletView> implements
        TransactionRecyclerViewAdapter.OnListInteractionListener {
    private static final String LOG_TAG = "OstWalletPresenter";

    private TransactionRecyclerViewAdapter mTransactionRecyclerViewAdapter;

    public static WalletPresenter newInstance() {
        return new WalletPresenter();
    }

    private JSONObject nextPayload = new JSONObject();
    private Boolean hasMoreData = false;
    private Boolean httpRequestPending = false;

    private List<Transaction> transactionList;

    @Override
    public void attachView(WalletView mvpView) {
        super.attachView(mvpView);
        transactionList = new ArrayList<>();
        //update balance as soon as the view gets attached
        updateBalance();
        createRecyclerViewAdapter();
        updateTransactionHistory(true);
    }

    private void createRecyclerViewAdapter() {
        mTransactionRecyclerViewAdapter = TransactionRecyclerViewAdapter.newInstance(transactionList, this);
    }

    void updateTransactionHistory(Boolean clearList) {
        if(httpRequestPending){
            return;
        }
        if(clearList){
            transactionList.clear();
            nextPayload = new JSONObject();
        } else if(!hasMoreData){
            return;
        }
        httpRequestPending = true;
        AppProvider.get().getMappyClient().getCurrentUserTransactions(nextPayload, new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (new CommonUtils().isValidResponse(jsonObject)) {
                    try {
                        JSONObject dataJSONObject =  new CommonUtils().parseJSONData(jsonObject);
                        nextPayload = dataJSONObject.optJSONObject("meta");
                        hasMoreData = (nextPayload != null && !nextPayload.getJSONObject("next_page_payload").toString().equals("{}"));
                        JSONArray transactionJSONArray = (JSONArray) new CommonUtils()
                                .parseResponseForResultType(jsonObject);

                        for (int i = 0; i < transactionJSONArray.length(); i++) {
                            JSONObject txnJSONObject = transactionJSONArray.getJSONObject(i);
                            List<Transaction> list = Transaction.newInstance(txnJSONObject);
                            transactionList.addAll(list);
                        }
                    } catch (JSONException e) {
                        //Exception not expected
                    }
                    getMvpView().notifyDataSetChanged();
                } else {
                    Log.e(LOG_TAG, String.format("Get Current User Transaction response false: %s", jsonObject.toString()));
                    getMvpView().notifyDataSetChanged();
                }
                httpRequestPending = false;
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(LOG_TAG, "Get Current User Transaction error");
                getMvpView().notifyDataSetChanged();
                httpRequestPending = false;
            }
        });
    }

    void updateBalance() {
        AppProvider.get().getMappyClient().getCurrentUserBalance(new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                String balance = "0";
                JSONObject pricePoint = null;
                if (new CommonUtils().isValidResponse(jsonObject)) {
                    balance = new CommonUtils().parseStringResponseForKey(jsonObject, "available_balance");
                    try{
                        JSONObject jsonData = jsonObject.getJSONObject(OstConstants.RESPONSE_DATA);
                        pricePoint = jsonData.optJSONObject("price_point");
                    } catch(Exception e){ }
                }
                AppProvider.get().getCurrentUser().updateBalance(balance);
                getMvpView().updateBalance(CommonUtils.convertWeiToTokenCurrency(balance),
                        CommonUtils.convertBTWeiToUsd(balance, pricePoint));
            }

            @Override
            public void onFailure(Throwable throwable) {
                getMvpView().updateBalance("0", null);
            }
        });


        //Test OstJsonApi - @Pankaj.
        OstJsonApi.getBalanceWithPricePoints(AppProvider.get().getCurrentUser().getOstUserId(), new OstJsonApiCallback() {
            @Override
            public void onOstJsonApiSuccess(@Nullable JSONObject data) {
                if ( null != data ) {
                    Log.d(LOG_TAG, "getBalanceWithPricePoints data: " + data.toString());
                } else {
                    Log.d(LOG_TAG, "getBalanceWithPricePoints data is null.");
                }
            }

            @Override
            public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject data) {
                Log.e(LOG_TAG, "getBalanceWithPricePoints InternalErrorCode:" + err.getInternalErrorCode());
                if ( null != data ) {
                    Log.d(LOG_TAG, "getBalanceWithPricePoints data: " + data.toString());
                } else {
                    Log.d(LOG_TAG, "getBalanceWithPricePoints data is null.");
                }
            }
        });
    }

    @Override
    public void onListViewInteraction(Transaction transaction) {
        getMvpView().openTransactionView(transaction);
    }

    public TransactionRecyclerViewAdapter getTransactionRecyclerViewAdapter() {
        return mTransactionRecyclerViewAdapter;
    }
}