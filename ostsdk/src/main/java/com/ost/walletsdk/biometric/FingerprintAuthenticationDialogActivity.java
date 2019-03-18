/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.biometric;

import android.app.Activity;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ost.walletsdk.R;

/**
 * A dialog which uses fingerprint APIs to authenticate the user, and falls back to password
 * authentication if fingerprint is not available.
 */
public class FingerprintAuthenticationDialogActivity extends Activity
        implements FingerprintUiHelper.Callback {

    private static final String TAG = "BiometricDailog";
    private TextView mCancelButton;

    private FingerprintUiHelper mFingerprintUiHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_dialog_container);
        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setFinishOnTouchOutside(false);
        mCancelButton = findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onError();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintUiHelper = new FingerprintUiHelper(
                    getSystemService(FingerprintManager.class),
                    findViewById(R.id.fingerprint_icon),
                    findViewById(R.id.fingerprint_status), this);
        } else {
            Log.e(TAG, "Bio metric is not supported for api less than 23");
            finish();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mFingerprintUiHelper.startListening(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    @Override
    public void onAuthenticated() {
        // Callback from FingerprintUiHelper. Let the activity know that authentication was
        // successful.
        Intent intent = new Intent(OstBiometricAuthentication.INTENT_FILTER_FINGERPRINT_AUTH);
        intent.putExtra(OstBiometricAuthentication.IS_AUTHENTICATED, true);
        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(intent);
        finish();
    }

    @Override
    public void onError() {
        Intent intent = new Intent(OstBiometricAuthentication.INTENT_FILTER_FINGERPRINT_AUTH);
        intent.putExtra(OstBiometricAuthentication.IS_AUTHENTICATED, false);
        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        onError();
        super.onBackPressed();
    }
}