package com.ost.mobilesdk.utils;

import android.util.Log;

import org.web3j.utils.Numeric;

public class TokenHolder {
    private static final String TAG = "OstTokenHolder";
    private static final String EXECUTABLE_CALL_STRING = "executeRule(address,bytes,uint256,uint8,bytes32,bytes32)";

    public TokenHolder() {
    }

    public String get_EXECUTABLE_CALL_PREFIX() {
        byte[] feed = EXECUTABLE_CALL_STRING.getBytes();
        String hash = null;
        try {
            hash = new SoliditySha3().soliditySha3(Numeric.toHexString(feed));
        } catch (Exception e) {
            Log.e(TAG, "Unexpected Exception");
        }
        hash = hash.substring(0,10);
        return hash;
    }
}