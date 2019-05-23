package com.ost.walletsdk.ecKeyInteracts;

public class OstBiometricManager {

    private final String mUserId;

    public OstBiometricManager(String userId) {
        mUserId = userId;
    }

    public void enableBiometric() {
        new InternalKeyManager(mUserId).setBiometricPreference(true);
    }

    public void disableBiometric() {
        new InternalKeyManager(mUserId).setBiometricPreference(false);
    }
}