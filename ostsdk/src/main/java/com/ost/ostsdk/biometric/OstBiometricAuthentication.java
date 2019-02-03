package com.ost.ostsdk.biometric;

import android.content.Context;

public class OstBiometricAuthentication {
    private final Context mContext;

    public OstBiometricAuthentication(Context context, Callback callback) {
        this.mContext = context;
        FingerprintAuthenticationDialogFragment fragment = new FingerprintAuthenticationDialogFragment();
        fragment.setCallbacks(callback);
        fragment.onAttach(context);
    }

    public interface Callback {

        void onAuthenticated();

        void onError();
    }

}