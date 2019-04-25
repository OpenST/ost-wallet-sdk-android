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
import android.content.Context;
import android.content.SharedPreferences;

import ost.com.demoapp.entity.CurrentEconomy;

import static ost.com.demoapp.entity.CurrentEconomy.MAPPY_API_ENDPOINT;
import static ost.com.demoapp.entity.CurrentEconomy.SAAS_API_ENDPOINT;
import static ost.com.demoapp.entity.CurrentEconomy.TOKEN_ID;
import static ost.com.demoapp.entity.CurrentEconomy.TOKEN_NAME;
import static ost.com.demoapp.entity.CurrentEconomy.TOKEN_SYMBOL;
import static ost.com.demoapp.entity.CurrentEconomy.URL_ID;
import static ost.com.demoapp.entity.CurrentEconomy.VIEW_API_ENDPOINT;

public class App extends Application {

    private CurrentEconomy currentEconomy;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("CurrentEconomy", Context.MODE_PRIVATE);
    }

    public CurrentEconomy getCurrentEconomy() {
        if (null != currentEconomy) {
            return currentEconomy;
        }

        //load pref id there are any
        currentEconomy = getCurrentEconomyPref();

        return currentEconomy;
    }

    private CurrentEconomy getCurrentEconomyPref() {
        String tokenName = sharedPreferences.getString(TOKEN_NAME, null);
        String tokenSymbol = sharedPreferences.getString(CurrentEconomy.TOKEN_SYMBOL, null);
        String tokenId = sharedPreferences.getString(CurrentEconomy.TOKEN_ID, null);
        String urlId = sharedPreferences.getString(CurrentEconomy.URL_ID, null);
        String mappyApiEndpoint = sharedPreferences.getString(CurrentEconomy.MAPPY_API_ENDPOINT, null);
        String saasApiEndpoint = sharedPreferences.getString(CurrentEconomy.SAAS_API_ENDPOINT, null);
        String viewApiEndpoint = sharedPreferences.getString(CurrentEconomy.VIEW_API_ENDPOINT, null);

        if (null == urlId || null == tokenName || null == tokenId
                || null == mappyApiEndpoint || null == saasApiEndpoint
                || null == viewApiEndpoint || null == tokenSymbol) {
            return null;
        }

        return new CurrentEconomy(
                tokenName,
                tokenId,
                tokenSymbol,
                urlId,
                mappyApiEndpoint,
                saasApiEndpoint,
                viewApiEndpoint
        );
    }

    public void setCurrentEconomy(CurrentEconomy currentEconomy) {
        this.currentEconomy = currentEconomy;
        SharedPreferences.Editor keyValuesEditor = sharedPreferences.edit();

        if (null == currentEconomy) {
            keyValuesEditor.clear();
        } else {
            keyValuesEditor.putString(TOKEN_NAME, currentEconomy.getTokenName());
            keyValuesEditor.putString(TOKEN_ID, currentEconomy.getTokenId());
            keyValuesEditor.putString(TOKEN_SYMBOL, currentEconomy.getTokenSymbol());
            keyValuesEditor.putString(URL_ID, currentEconomy.getUrlId());
            keyValuesEditor.putString(MAPPY_API_ENDPOINT, currentEconomy.getMappyApiEndpoint());
            keyValuesEditor.putString(SAAS_API_ENDPOINT, currentEconomy.getSaasApiEndpoint());
            keyValuesEditor.putString(VIEW_API_ENDPOINT, currentEconomy.getViewApiEndpoint());
        }

        keyValuesEditor.apply();
    }
}