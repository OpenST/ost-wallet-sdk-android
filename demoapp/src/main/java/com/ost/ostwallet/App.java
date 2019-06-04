/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet;

import android.app.Application;

import com.ost.ostwallet.entity.CurrentEconomy;
import com.ost.walletsdk.OstSdk;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

import com.ost.ostwallet.network.PersistentCookieStore;

import org.json.JSONException;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize persistent cookie storage
        CookieStore persistentCookieStore = new PersistentCookieStore(this);
        CookieManager cookieManager = new CookieManager(persistentCookieStore,
                CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        AppProvider.init(getApplicationContext());
        AppProvider.get().setPersistentCookieStore(persistentCookieStore);

        CurrentEconomy currentEconomy;
        try {
            currentEconomy = CurrentEconomy.newInstance("{\"token_id\":1141,\"token_name\":\"T21053\",\"token_symbol\":\"T213\",\"url_id\":\"7f77d96c05c1c18c8b42cf537a2676653d7d99ffff2d013152fb4f402b7e8e23\",\"mappy_api_endpoint\":\"https://demo-mappy.stagingost.com/demo/\",\"saas_api_endpoint\":\"https://api.stagingost.com/testnet/v2/\",\"view_api_endpoint\":\"https://view.stagingost.com/testnet/\"}");
        } catch (JSONException e) {
            throw  new RuntimeException("In App");
        }
        AppProvider.get().setCurrentEconomy(currentEconomy);

        if (null != AppProvider.get().getCurrentEconomy()) {
            OstSdk.initialize(getApplicationContext(), AppProvider.get().getCurrentEconomy().getSaasApiEndpoint());
        }
    }
}