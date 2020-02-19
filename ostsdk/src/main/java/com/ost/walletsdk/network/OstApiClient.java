/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.network;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.OstApiSigner;
import com.ost.walletsdk.ecKeyInteracts.OstKeyManager;
import com.ost.walletsdk.models.entities.OstToken;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Http api client over
 *
 * @see OstHttpRequestClient
 * specific for OST Platform calls
 */
public class OstApiClient {
    private static final String TAG = "OstApiClient";

    private static final String API_KEY = "api_key";
    private static final String API_REQUEST_TIMESTAMP = "api_request_timestamp";
    private static final String API_SIGNATURE_KIND = "api_signature_kind";
    private static final String TOKEN_ID = "token_id";
    private static final String USER_ID = "user_id";
    private static final String SIG_TYPE = "OST1-PS";
    private static final String SESSION_ADDRESSES = "session_addresses";
    private static final String EXPIRATION_HEIGHT = "expiration_height";
    private static final String SPENDING_LIMIT = "spending_limit";
    private static final String RECOVERY_OWNER_ADDRESS = "recovery_owner_address";
    private static final String DEVICE_ADDRESS = "device_address";
    private final OstHttpRequestClient mOstHttpRequestClient;
    private final String mUserId;
    private final OstUser mOstUser;
    private final OstHttpRequestClient.ResponseParser mResponseParser;
    private final OstApiSigner mApiSigner;

    public OstHttpRequestClient.ResponseParser getResponseParser() {
        return mResponseParser;
    }

    public OstApiClient(String userId, String baseUrl, boolean enableResponseLog) {
        mUserId = userId;
        mOstUser = OstSdk.getUser(userId);

        mOstHttpRequestClient = new OstHttpRequestClient(baseUrl, enableResponseLog);
        mApiSigner = new OstApiSigner(mUserId);
        mOstHttpRequestClient.setOstApiSigner(mApiSigner);

        mResponseParser = new OstApiHelper(userId);
        mOstHttpRequestClient.setResponseParser(mResponseParser);
    }

    public OstApiClient(String userId) {
        this(userId, OstSdk.get().get_BASE_URL(), true);
    }

    public OstApiClient(String userId, boolean enableResponseLog) {
        this(userId, OstSdk.get().get_BASE_URL(), enableResponseLog);
    }

    public OstHttpRequestClient getOstHttpRequestClient() {
        return mOstHttpRequestClient;
    }

    public JSONObject getToken() throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get("/tokens/", requestMap);
    }

    public JSONObject postUserActivate(List<String> sessionAddresses,
                                       String expirationHeight,
                                       String spendingLimit,
                                       String recoveryOwnerAddress) throws OstError {

        Map<String, Object> requestMap = new HashMap<>();

        requestMap.put(SESSION_ADDRESSES, sessionAddresses);
        requestMap.put(EXPIRATION_HEIGHT, expirationHeight);
        requestMap.put(SPENDING_LIMIT, spendingLimit);
        requestMap.put(RECOVERY_OWNER_ADDRESS, recoveryOwnerAddress);
        requestMap.put(DEVICE_ADDRESS, mOstUser.getCurrentDevice().getAddress());
        return postUserActivate(requestMap);
    }

    public JSONObject postUserActivate(Map<String, Object> map) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/activate-user/", mUserId), requestMap);
    }

    public JSONObject getDevice(String address) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/devices/%s/", mUserId, address), requestMap);
    }

    public JSONObject getUser() throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s", mUserId), requestMap);
    }

    private Map<String, Object> getPrerequisiteMap() {
        Map<String, Object> map = new HashMap<>();

        // api token_id.user_id.device_address.personal_sign_address
        map.put(API_KEY, String.format("%s.%s.%s.%s", mOstUser.getTokenId(), mUserId,
                mOstUser.getCurrentDevice().getAddress(),
                mOstUser.getCurrentDevice().getApiSignerAddress()));
        map.put(API_REQUEST_TIMESTAMP, String.valueOf((int) (System.currentTimeMillis() / 1000)));
        map.put(API_SIGNATURE_KIND, SIG_TYPE);
        return map;
    }

    public JSONObject getSalt() throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/salts", mUserId), requestMap);
    }

    public JSONObject getCurrentBlockNumber() throws OstError {
        String tokenId = mOstUser.getTokenId();
        OstToken ostToken = OstToken.getById(tokenId);
        String chainId = ostToken.getChainId();
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/chains/%s", chainId), requestMap);
    }

    public JSONObject postAddDevice(Map<String, Object> map) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/devices/authorize", mUserId), requestMap);
    }

    public JSONObject postAddSession(Map<String, Object> map) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/sessions/authorize", mUserId), requestMap);
    }

    public JSONObject getSession(String address) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        JSONObject jsonObject = null;
        try {
            jsonObject = mOstHttpRequestClient.get(String.format("/users/%s/sessions/%s", mUserId, address), requestMap);
        } catch (OstApiError ostApiError) {
            try {
                new OstKeyManager(mUserId).handleSessionApiError(ostApiError, address);
            } catch (Throwable th) {
                //Do nothing
            }
            throw ostApiError;
        }
        return jsonObject;
    }

    public JSONObject getDeviceManager() throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/device-managers", mUserId), requestMap);
    }

    public JSONObject postExecuteTransaction(Map<String, Object> map) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/transactions", mUserId), requestMap);
    }

    public JSONObject getTransaction(String transactionId) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/transactions/%s", mUserId, transactionId), requestMap);
    }

    public JSONObject getTransactions(Map<String, Object> map) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        if ( null != map ) {
            requestMap.putAll(map);
        }
        return mOstHttpRequestClient.get(String.format("/users/%s/transactions", mUserId), requestMap);
    }


    public JSONObject getRedeemableSkus(Map<String, Object> map) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        if ( null != map ) {
            requestMap.putAll(map);
        }
        return mOstHttpRequestClient.get(String.format("/redeemable-skus", mUserId), requestMap);
    }

    public JSONObject getRedeemableSkuDetails(String skuId , Map<String, Object> map) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        if ( null != map ) {
            requestMap.putAll(map);
        }
        return mOstHttpRequestClient.get(String.format("/redeemable-skus/%s", mUserId, skuId), requestMap);
    }

    public JSONObject getAllRules() throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get("/rules", requestMap);
    }

    public JSONObject postRecoveryOwners(Map<String, Object> map) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/recovery-owners", mUserId), requestMap);
    }

    public JSONObject getRecoveryOwnerAddress(String recoveryAddress) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/recovery-owners/%s", mUserId, recoveryAddress), requestMap);
    }

    public JSONObject postRevokeDevice(Map<String, Object> map) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/devices/revoke", mUserId), requestMap);
    }

    public JSONObject postInitiateRecovery(Map<String, Object> map) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/devices/initiate-recovery", mUserId), requestMap);
    }

    public JSONObject postAbortRecovery(Map<String, Object> map) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/devices/abort-recovery", mUserId), requestMap);
    }

    public JSONObject getPricePoints() throws OstError {
        String tokenId = mOstUser.getTokenId();
        OstToken ostToken = OstToken.getById(tokenId);
        String chainId = ostToken.getChainId();
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/chains/%s/price-points", chainId), requestMap);
    }

    public JSONObject getPendingRecovery() throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/devices/pending-recovery", mUserId), requestMap);
    }

    public JSONObject getBalance() throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/balance", mUserId), requestMap);
    }

    public JSONObject postLogoutAllSessions(Map<String, Object> map) throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/token-holder/logout", mUserId), requestMap);
    }

    public JSONObject getTokenHolder() throws OstError {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/token-holder", mUserId), requestMap);
    }

    public JSONObject getDeviceList(Map<String, Object> map) {
        Map<String, Object> requestMap = getPrerequisiteMap();
        if ( null != map ) {
            requestMap.putAll(map);
        }
        return mOstHttpRequestClient.get(String.format("/users/%s/devices", mUserId), requestMap);
    }
}