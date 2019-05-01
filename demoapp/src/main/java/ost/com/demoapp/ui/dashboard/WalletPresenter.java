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

import org.json.JSONObject;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.ui.BasePresenter;
import ost.com.demoapp.util.CommonUtils;

class WalletPresenter extends BasePresenter<WalletView> {
    public static WalletPresenter newInstance() {
        return new WalletPresenter();
    }

    @Override
    public void attachView(WalletView mvpView) {
        super.attachView(mvpView);

        //update balance as soon as the view gets attached
        updateBalance();
    }

    private void updateBalance() {
        AppProvider.get().getMappyClient().getCurrentUserBalance(new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                String balance = "Not able to fetch Error";
                if (new CommonUtils().isValidResponse(jsonObject)) {
                    balance = new CommonUtils().parseStringResponseForKey(jsonObject, "available_balance");
                }
                getMvpView().updateBalance(balance);
            }

            @Override
            public void onFailure(Throwable throwable) {
                getMvpView().updateBalance("Balance fetch error");
            }
        });
    }

}