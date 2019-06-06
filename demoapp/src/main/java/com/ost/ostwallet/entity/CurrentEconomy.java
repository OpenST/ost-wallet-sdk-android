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

import org.json.JSONException;
import org.json.JSONObject;

public class CurrentEconomy {
    public static final String TOKEN_NAME = "token_name";
    public static final String TOKEN_SYMBOL = "token_symbol";
    public static final String TOKEN_ID = "token_id";
    public static final String URL_ID = "url_id";
    public static final String MAPPY_API_ENDPOINT = "mappy_api_endpoint";
    public static final String SAAS_API_ENDPOINT = "saas_api_endpoint";
    public static final String VIEW_API_ENDPOINT = "view_api_endpoint";
    private final String tokenName;
    private final String tokenId;
    private final String tokenSymbol;
    private final String urlId;
    private final String mappyApiEndpoint;
    private final String saasApiEndpint;
    private final String viewApiEndpoint;

    public CurrentEconomy(
            String tokenName,
            String tokenId,
            String tokenSymbol,
            String urlId,
            String mappyApiEndpoint,
            String saasApiEndpoint,
            String viewApiEndpoint
    ) {

        this.tokenName = tokenName;
        this.tokenId = tokenId;
        this.tokenSymbol = tokenSymbol;
        this.urlId = urlId;
        this.mappyApiEndpoint = mappyApiEndpoint;
        this.saasApiEndpint = saasApiEndpoint;
        this.viewApiEndpoint = viewApiEndpoint;
    }

    public static CurrentEconomy newInstance(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        return new CurrentEconomy(
                jsonObject.getString(TOKEN_NAME),
                jsonObject.getString(TOKEN_ID),
                jsonObject.getString(TOKEN_SYMBOL),
                jsonObject.getString(URL_ID),
                jsonObject.getString(MAPPY_API_ENDPOINT),
                jsonObject.getString(SAAS_API_ENDPOINT),
                jsonObject.getString(VIEW_API_ENDPOINT)

        );
    }

    public String getTokenName() {
        return tokenName;
    }

    public String getTokenId() {
        return tokenId;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public String getUrlId() {
        return urlId;
    }

    public String getMappyApiEndpoint() {
        return mappyApiEndpoint;
    }

    public String getSaasApiEndpoint() {
        return saasApiEndpint;
    }

    public String getViewApiEndpoint() {
        return viewApiEndpoint;
    }
}
