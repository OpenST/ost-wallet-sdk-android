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

import com.ost.walletsdk.OstSdk;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

import com.ost.ostwallet.network.PersistentCookieStore;

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
    }
}