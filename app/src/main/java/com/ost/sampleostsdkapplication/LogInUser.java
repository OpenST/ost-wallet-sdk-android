/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.sampleostsdkapplication;

import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstUser;

import org.json.JSONException;
import org.json.JSONObject;

public class LogInUser {
    private static final String TAG = "LogInUser";
    private String id;
    private String ostUserId;
    private String tokenId;
    private String passphrasePrefix;

    LogInUser(JSONObject jsonObject) {
        try {
            id = jsonObject.getString(Constants.APP_USER_ID);
            ostUserId = jsonObject.getString(Constants.USER_ID);
            tokenId = jsonObject.getString(Constants.TOKEN_ID);
            passphrasePrefix = jsonObject.getString(Constants.USER_PIN_SALT);
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception", e.getCause());
        }
    }

    public LogInUser(String userId, String appId, String tokenId, String userPinSalt) {
        this.id = appId;
        this.ostUserId = userId;
        this.tokenId = tokenId;
        this.passphrasePrefix = userPinSalt;
    }

    public String getId() {
        return id;
    }

    public String getOstUserId() {
        return ostUserId;
    }

    public OstUser getOstUser() {
        return OstSdk.getUser(ostUserId);
    }


    public String getTokenId() {
        return tokenId;
    }

    public String getPassphrasePrefix() {
        return passphrasePrefix;
    }
}