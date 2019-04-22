/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.sampleostsdkapplication;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.ost.walletsdk.OstSdk;

public class App extends Application {


    private static String BASE_URL_MAPPY;
    private static String BASE_URL_OST_PLATFORM;

    private LogInUser loggedUser;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        BASE_URL_MAPPY = getString(R.string.base_url_mappy);
        BASE_URL_OST_PLATFORM = getString(R.string.base_url_ost_platform);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            OstSdk.initialize(getApplicationContext(), getBaseUrlOstPlatform());
        }

        sharedPreferences = getSharedPreferences("LoggedIn_user", Context.MODE_PRIVATE);
    }

    public LogInUser getLoggedUser() {
        if (null != loggedUser) {
            return loggedUser;
        }

        //load pref id there are any
        loggedUser = getLoggedUserFromPref();

        return loggedUser;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private LogInUser getLoggedUserFromPref() {
        String userId = sharedPreferences.getString(Constants.USER_ID, null);
        String appId = sharedPreferences.getString(Constants.APP_USER_ID, null);
        String tokenId = sharedPreferences.getString(Constants.TOKEN_ID, null);
        String userPinSalt = sharedPreferences.getString(Constants.USER_PIN_SALT, null);

        if (null == userId || null == appId || null == tokenId || null == userPinSalt) {
            return null;
        }

        return new LogInUser(userId, appId, tokenId, userPinSalt);
    }

    public void setLoggedUser(LogInUser loggedUser) {
        this.loggedUser = loggedUser;
        SharedPreferences.Editor keyValuesEditor = sharedPreferences.edit();

        if (null == loggedUser) {
            keyValuesEditor.clear();
        } else {
            keyValuesEditor.putString(Constants.USER_ID, loggedUser.getOstUserId());
            keyValuesEditor.putString(Constants.APP_USER_ID, loggedUser.getId());
            keyValuesEditor.putString(Constants.TOKEN_ID, loggedUser.getTokenId());
            keyValuesEditor.putString(Constants.USER_PIN_SALT, loggedUser.getPassphrasePrefix());
        }

        keyValuesEditor.apply();
    }

    public static String getBaseUrlOstPlatform() {
        return BASE_URL_OST_PLATFORM;
    }

    public static String getBaseUrlMappy() {
        return BASE_URL_MAPPY;
    }
}