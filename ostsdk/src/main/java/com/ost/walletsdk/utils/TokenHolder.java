/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.utils;

import android.util.Log;

import com.ost.walletsdk.OstConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.utils.Numeric;

import java.util.Collections;

public class TokenHolder {
    private static final String TAG = "OstTokenHolder";
    private static final String EXECUTABLE_CALL_STRING = "executeRule(address,bytes,uint256,bytes32,bytes32,uint8)";
    private static final String LOGOUT_ALL_SESSIONS = "logout";

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


    public String getLogoutExecutableData() {
        Function function = new Function(
                LOGOUT_ALL_SESSIONS,  // function we're calling
                Collections.emptyList(),  // Parameters to pass as Solidity Types
                Collections.emptyList()
        );

        return FunctionEncoder.encode(function);
    }


    public String getLogoutData() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(OstConstants.METHOD, LOGOUT_ALL_SESSIONS);

            //There are no parameters for logout method
            JSONArray jsonArray = new JSONArray();
            jsonObject.put(OstConstants.PARAMETERS, jsonArray);
            return jsonObject.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected exception while parsing json");
        }
        return null;
    }
}