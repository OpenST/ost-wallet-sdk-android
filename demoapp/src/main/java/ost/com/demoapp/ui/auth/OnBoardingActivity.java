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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ost.com.demoapp.R;
import ost.com.demoapp.ui.BaseActivity;
import ost.com.demoapp.ui.dashboard.DashboardActivity;
import ost.com.demoapp.ui.qrscanner.QRScannerActivity;
import ost.com.demoapp.util.FragmentUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class OnBoardingActivity extends BaseActivity implements
        OnBoardingView,
        IntroFragment.OnFragmentInteractionListener,
        CreateAccountFragment.OnFragmentInteractionListener {

    private static final int QR_REQUEST_CODE = 2;
    private static final String LOG_TAG = "OstOnBoardingActivity";
    OnBoardingPresenter mOnBoardingPresenter = OnBoardingPresenter.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_onboarding);

        mOnBoardingPresenter.attachView(this);

        mOnBoardingPresenter.checkLoggedInUser();
        FragmentUtils.clearBackStackAndAddFragment(R.id.layout_container,
                IntroFragment.newInstance(),
                this);
    }

    @Override
    public void launchCreateAccountView() {
        FragmentUtils.addFragment(R.id.layout_container,
                CreateAccountFragment.newInstance(true),
                this);
    }

    @Override
    public void launchLoginView() {
        FragmentUtils.addFragment(R.id.layout_container,
                CreateAccountFragment.newInstance(false),
                this);
    }

    @Override
    public void goBack() {
        if (!FragmentUtils.isBackStackEmpty(this)) {
            FragmentUtils.goBack(this);
        }
    }

    @Override
    public void createAccount(String economy, String userName, String password) {
        mOnBoardingPresenter.createAccount(userName, password);
    }

    @Override
    public void logIn(String economy, String userName, String password) {
        mOnBoardingPresenter.logIn(userName, password);
    }

    @Override
    public void scanForEconomy() {
        Intent intent = new Intent(getApplicationContext(), QRScannerActivity.class);
        startActivityForResult(intent, QR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String returnedResult = data.getData().toString();
            try {
                Log.w(LOG_TAG, returnedResult);
                mOnBoardingPresenter.onScanEconomyResult(returnedResult);
            } catch (Exception e) {
                Log.e(LOG_TAG, "JSONException while parsing");
            }
        }
    }

    @Override
    public void refreshToken() {
        ((CreateAccountFragment)FragmentUtils.getTopFragment(this,R.id.layout_container)).updateToken();
    }

    @Override
    public void goToDashBoard() {
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
        animateActivityChangingToRight();
        finish();
    }
}