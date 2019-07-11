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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.ost.walletsdk.OstSdk;

import java.net.CookieStore;

import com.ost.ostwallet.database.OstAppDatabase;
import com.ost.ostwallet.entity.CurrentEconomy;
import com.ost.ostwallet.entity.LogInUser;
import com.ost.ostwallet.network.MappyNetworkClient;
import com.ost.ostwallet.network.NetworkClient;
import com.ost.ostwallet.ui.BaseActivity;
import com.ost.ostwallet.ui.auth.OnBoardingActivity;
import com.ost.ostwallet.util.DBLog;

import static com.ost.ostwallet.entity.CurrentEconomy.MAPPY_API_ENDPOINT;
import static com.ost.ostwallet.entity.CurrentEconomy.SAAS_API_ENDPOINT;
import static com.ost.ostwallet.entity.CurrentEconomy.TOKEN_ID;
import static com.ost.ostwallet.entity.CurrentEconomy.TOKEN_NAME;
import static com.ost.ostwallet.entity.CurrentEconomy.TOKEN_SYMBOL;
import static com.ost.ostwallet.entity.CurrentEconomy.URL_ID;
import static com.ost.ostwallet.entity.CurrentEconomy.VIEW_API_ENDPOINT;

public class AppProvider {
    private static final String LOG_TAG = "AppProvider";

    private static final String POST_CRASH_ANALYTICS = "post_crash_analytics";
    private static AppProvider INSTANCE = null;
    private final Context mApplicationContext;
    private final SharedPreferences sharedPreferencesEconomy;
    private final SharedPreferences sharedPreferencesCrashAnalytics;
    private CurrentEconomy currentEconomy;
    private LogInUser logInUser;
    private CookieStore mCookieStore;
    private BaseActivity mCurrentActivity;

    private AppProvider(Context context) {
        mApplicationContext = context;
        NetworkClient.init(mApplicationContext);
        sharedPreferencesEconomy = mApplicationContext.getSharedPreferences("CurrentEconomy", Context.MODE_PRIVATE);
        sharedPreferencesCrashAnalytics = mApplicationContext.getSharedPreferences("CrashAnalytics", Context.MODE_PRIVATE);
        getCurrentEconomy();
        getPostCrashAnalytics();
    }

    public static void init(Context context) {
        INSTANCE = new AppProvider(context);
        OstAppDatabase.initDatabase(context);
    }

    public static AppProvider get() {
        if (null == INSTANCE) {
            throw new RuntimeException("AppProvider not initialized");
        }
        return INSTANCE;
    }

    public MappyNetworkClient getMappyClient() {
        CurrentEconomy currentEconomy = getCurrentEconomy();

        return new MappyNetworkClient(
                String.format("%sapi/%s/%s/", getCurrentEconomy().getMappyApiEndpoint(), currentEconomy.getTokenId(), currentEconomy.getUrlId()),
                NetworkClient.getRequestQueue()
        );
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
        String tokenName = sharedPreferencesEconomy.getString(TOKEN_NAME, null);
        String tokenSymbol = sharedPreferencesEconomy.getString(CurrentEconomy.TOKEN_SYMBOL, null);
        String tokenId = sharedPreferencesEconomy.getString(CurrentEconomy.TOKEN_ID, null);
        String urlId = sharedPreferencesEconomy.getString(CurrentEconomy.URL_ID, null);
        String mappyApiEndpoint = sharedPreferencesEconomy.getString(CurrentEconomy.MAPPY_API_ENDPOINT, null);
        String saasApiEndpoint = sharedPreferencesEconomy.getString(CurrentEconomy.SAAS_API_ENDPOINT, null);
        String viewApiEndpoint = sharedPreferencesEconomy.getString(CurrentEconomy.VIEW_API_ENDPOINT, null);

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
        SharedPreferences.Editor keyValuesEditor = sharedPreferencesEconomy.edit();

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
        try {
            //Initialize SDK
            OstSdk.initialize(mApplicationContext, this.currentEconomy.getSaasApiEndpoint());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    public boolean isPostCrashAnalyticsSet() {
        return (0 <= sharedPreferencesCrashAnalytics.getInt(POST_CRASH_ANALYTICS, -1));
    }

    public boolean getPostCrashAnalytics() {
        int postCrashAnalytics = sharedPreferencesCrashAnalytics.getInt(POST_CRASH_ANALYTICS, 0);
        return (postCrashAnalytics != 0);
    }

    public void setPostCrashAnalytics(boolean shouldPost) {
        SharedPreferences.Editor keyValuesEditor = sharedPreferencesCrashAnalytics.edit();
        keyValuesEditor.putInt(POST_CRASH_ANALYTICS, shouldPost ? 1 : 0);
        keyValuesEditor.apply();
    }

    public Context getApplicationContext() {
        return mApplicationContext;
    }

    public void setCurrentUser(LogInUser logInUser) {
        this.logInUser = logInUser;
    }

    public LogInUser getCurrentUser() {
        return logInUser;
    }

    public void setPersistentCookieStore(CookieStore persistentCookieStore) {
        mCookieStore = persistentCookieStore;
    }

    public CookieStore getCookieStore() {
        return mCookieStore;
    }

    public DBLog getDBLogger() {
        return new DBLog();
    }

    public void relaunchApp() {
        AppProvider.get().getCookieStore().removeAll();

        Intent intent = new Intent(mApplicationContext, OnBoardingActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(mApplicationContext, mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) mApplicationContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public void setCurrentActivity(BaseActivity baseActivity) {
        mCurrentActivity = baseActivity;
    }

    public BaseActivity getCurrentActivity() {
        return mCurrentActivity;
    }
}
