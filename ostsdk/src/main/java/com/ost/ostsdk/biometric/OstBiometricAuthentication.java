package com.ost.ostsdk.biometric;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class OstBiometricAuthentication {
    public static final String INTENT_FILTER_FINGERPRINT_AUTH = "com.ost.ostsdk.biometric.FINGERPRINT_AUTH";
    public static final String IS_AUTHENTICATED = "is_authenticated";

    private final Context mContext;

    public OstBiometricAuthentication(Context context, Callback callback) {
        this.mContext = context;

        Intent intent = new Intent(mContext, FingerprintAuthenticationDialogActivity.class);

        BroadcastReceiver broadcastReceiver = new AuthenticationReceiver(callback);
        context.registerReceiver(broadcastReceiver, new IntentFilter(INTENT_FILTER_FINGERPRINT_AUTH));
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
            context.unregisterReceiver(this);
        }
    }
    public interface Callback {

        void onAuthenticated();

        void onError();
    }

}