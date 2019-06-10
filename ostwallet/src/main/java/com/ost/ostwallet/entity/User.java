/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.entity;

import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstUser;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private static final String TAG = "OstLogInUser";
    private static final String APP_USER_ID = "app_user_id";
    private static final String TOKEN_ID = "token_id";
    private static final String USER_NAME = "username";
    private static final String USER_ID = "user_id";
    private static final String TOKEN_HOLDER_ADDRESS = "token_holder_address";
    private static final String STATUS = "status";
    private static final String AVAILABLE_BALANCE = "available_balance";

    private final String tokenHolderAddress;
    private final String balance;
    private final String status;

    private String id;
    private String ostUserId;
    private String tokenId;
    private String userName;

    public static User newInstance(JSONObject usersJSONObject, JSONObject balancesJSONObject) {
        try {
            String id = usersJSONObject.getString(APP_USER_ID);
            String userName = usersJSONObject.getString(USER_NAME);
            String tokenId = usersJSONObject.getString(TOKEN_ID);
            String ostUserId = usersJSONObject.getString(USER_ID);
            String tokenHolderAddress = usersJSONObject.getString(TOKEN_HOLDER_ADDRESS);
            String status = usersJSONObject.getString(STATUS);
            String balance = "0";
            JSONObject balanceJsonObject = balancesJSONObject.optJSONObject(id);
            if (null != balanceJsonObject) {
                balance = balanceJsonObject.getString(AVAILABLE_BALANCE);
            }
            return new User(id, userName, tokenId, ostUserId, tokenHolderAddress, balance, status);
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception", e.getCause());
        }
        return null;
    }

    public User(String id, String userName, String tokenId, String ostUserId, String tokenHolderAddress, String balance, String status) {
        this.id = id;
        this.userName = userName;
        this.tokenId = tokenId;
        this.ostUserId = ostUserId;
        this.tokenHolderAddress = tokenHolderAddress;
        this.balance = balance;
        this.status = status;
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

    public String getUserName() {
        return userName;
    }

    public String getTokenHolderAddress() {
        return tokenHolderAddress;
    }

    public String getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }
}