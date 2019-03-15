/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.biometric;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class OstBiometricAuthentication {
    public static final String INTENT_FILTER_FINGERPRINT_AUTH = "com.ost.ostsdk.biometric.FINGERPRINT_AUTH";
    public static final String IS_AUTHENTICATED = "is_authenticated";

    private final Context mContext;

    public OstBiometricAuthentication(Context context, Callback callback) {
        this.mContext = context;

        Intent intent = new Intent(mContext, FingerprintAuthenticationDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        BroadcastReceiver broadcastReceiver = new AuthenticationReceiver(callback);
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(INTENT_FILTER_FINGERPRINT_AUTH));
        mContext.getApplicationContext().startActivity(intent);

    }

    public static class AuthenticationReceiver extends BroadcastReceiver {

        private final Callback mCallback;

        AuthenticationReceiver(Callback callback) {
            mCallback = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean is_authenticated = intent.getBooleanExtra(IS_AUTHENTICATED, false);
            if (is_authenticated) {
                mCallback.onAuthenticated();
            } else {
                mCallback.onError();
            }
            LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
        }
    }
    public interface Callback {

        void onAuthenticated();

        void onError();
    }

}