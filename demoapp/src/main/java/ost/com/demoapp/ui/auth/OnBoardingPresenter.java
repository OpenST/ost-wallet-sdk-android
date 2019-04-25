/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.auth;

import android.util.Log;

import org.json.JSONObject;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.ui.BasePresenter;

public class OnBoardingPresenter extends BasePresenter<OnBoardingView> {

    private static final String LOG_TAG = "OstOnBoardingPresenter";
    private final MappyNetworkClient mMappyNetworkClient;

    public static OnBoardingPresenter getInstance() {
        return new OnBoardingPresenter();
    }

    private OnBoardingPresenter() {
        mMappyNetworkClient = AppProvider.get().getMappyClient();
    }

    public void createAccount(String userName, String password) {
        getMvpView().showProgress(true);
        mMappyNetworkClient.createAccount(userName, password, new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                getMvpView().showProgress(false);
                Log.d(LOG_TAG, jsonObject.toString());
            }

            @Override
            public void onFailure(Throwable throwable) {
                getMvpView().showProgress(false);
                Log.e(LOG_TAG, throwable.getMessage());
            }
        });
    }

    public void logIn(String userName, String password) {
        getMvpView().showProgress(true);
        mMappyNetworkClient.logIn(userName, password, new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                getMvpView().showProgress(false);
                Log.d(LOG_TAG, jsonObject.toString());
            }

            @Override
            public void onFailure(Throwable throwable) {
                getMvpView().showProgress(false);
                Log.e(LOG_TAG, throwable.getMessage());
            }
        });
    }
}
