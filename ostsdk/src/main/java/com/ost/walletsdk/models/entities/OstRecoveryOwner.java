/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.models.entities;


import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Keys;

import java.util.Arrays;

import static com.ost.walletsdk.models.entities.OstRecoveryOwner.CONST_STATUS.AUTHORIZATION_FAILED;
import static com.ost.walletsdk.models.entities.OstRecoveryOwner.CONST_STATUS.AUTHORIZED;
import static com.ost.walletsdk.models.entities.OstRecoveryOwner.CONST_STATUS.AUTHORIZING;
import static com.ost.walletsdk.models.entities.OstRecoveryOwner.CONST_STATUS.REVOKED;
import static com.ost.walletsdk.models.entities.OstRecoveryOwner.CONST_STATUS.REVOKING;

/**
 * To hold RecoveryOwner info
 */
public class OstRecoveryOwner extends OstBaseEntity {

    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";
    private static EntityFactory entityFactory;

    public OstRecoveryOwner(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }


    public OstRecoveryOwner(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public static String getIdentifier() {
        return OstRecoveryOwner.ADDRESS;
    }

    public static boolean isValidStatus(String status) {
        return Arrays.asList(AUTHORIZATION_FAILED, AUTHORIZING, AUTHORIZED, REVOKING, REVOKED).contains(status);
    }

    private static EntityFactory getEntityFactory() {
        if (null == entityFactory) {
            entityFactory = new EntityFactory() {
                @Override
                public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                    return new OstRecoveryOwner(jsonObject);
                }
            };
        }
        return entityFactory;
    }

    public static OstRecoveryOwner parse(JSONObject jsonObject) throws JSONException {
        return (OstRecoveryOwner) getEntityFactory().createEntity(jsonObject);
    }

    @Override
    protected OstRecoveryOwner updateWithJsonObject(JSONObject jsonObject) throws JSONException {
        return OstRecoveryOwner.parse(jsonObject);
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstRecoveryOwner.USER_ID) &&
                jsonObject.has(OstRecoveryOwner.ADDRESS);


    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
    }

    public String getUserId() {
        return this.getParentId();
    }

    @Override
    public String getId() {
        String id = super.getId();
        id = Keys.toChecksumAddress(id);
        return id;
    }

    public String getAddress() {
        return this.getId();
    }

    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }

    @Override
    public String getParentIdKey() {
        return OstDevice.USER_ID;
    }

    public static class CONST_STATUS {
        public static final String AUTHORIZATION_FAILED = "authorizing_failed";
        public static final String AUTHORIZING = "authorizing";
        public static final String AUTHORIZED = "authorized";
        public static final String REVOKING = "revoking";
        public static final String REVOKED = "revoked";
    }
}