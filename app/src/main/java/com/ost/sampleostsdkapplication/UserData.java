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

import org.json.JSONObject;

public class UserData {
    private final String name;
    private final String mobile;
    private final String description;
    private final String id;
    private String tokenHolderAddress;

    public UserData(String id, String name, String mobile, String description, String tokenHolderAddress) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.description = description;
        this.tokenHolderAddress = tokenHolderAddress;
    }

    public static UserData parse(JSONObject jsonObject) {
        String id = jsonObject.optString(Constants.OST_USER_ID, "");
        String name = jsonObject.optString(Constants.USER_NAME, "");
        String mobile = jsonObject.optString(Constants.MOBILE_NUMBER,"");
        String description = jsonObject.optString(Constants.DESCRIPTION, "");
        String tokenHolderAddress = jsonObject.optString(Constants.TOKEN_HOLDER_ADDRESS, "");
        return new UserData(id, name, mobile, description, tokenHolderAddress);
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getTokenHolderAddress() {
        return tokenHolderAddress;
    }
}