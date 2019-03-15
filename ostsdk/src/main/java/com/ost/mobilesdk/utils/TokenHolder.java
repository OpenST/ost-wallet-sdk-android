/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

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