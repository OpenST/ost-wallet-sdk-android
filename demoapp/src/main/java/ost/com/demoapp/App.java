/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp;

import android.app.Application;

import org.json.JSONException;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import ost.com.demoapp.entity.CurrentEconomy;
import ost.com.demoapp.network.PersistentCookieStore;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CurrentEconomy currentEconomy;
        try {
            currentEconomy = CurrentEconomy.newInstance("{\"token_id\":1009,\"token_name\":\"BYTE BANDIT\",\"token_symbol\":\"BANT\",\"url_id\":\"f9ac2d85b0663bbd52925098db2e7c2851ff50ce97eebc2fb20f56b89a72a500\",\"mappy_api_endpoint\":\"https://demo-mappy.stagingost.com/demo/\",\"saas_api_endpoint\":\"https://api.stagingost.com/testnet/v2/\",\"view_api_endpoint\":\"https://view.stagingost.com/testnet/\"}");
        } catch (JSONException e) {
            throw  new RuntimeException("In App");
        }
        //Initialize persistent cookie storage
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(this),
                CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        AppProvider.init(getApplicationContext());
        AppProvider.get().setCurrentEconomy(currentEconomy);
    }
}