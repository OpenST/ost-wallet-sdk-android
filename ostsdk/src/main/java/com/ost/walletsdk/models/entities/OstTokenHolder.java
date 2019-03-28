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


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.text.TextUtils;

import com.ost.walletsdk.models.Impls.OstModelFactory;
import com.ost.walletsdk.models.Impls.OstSecureKeyModelRepository;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Keys;

import java.util.Arrays;

import static com.ost.walletsdk.models.entities.OstTokenHolder.CONST_STATUS.ACTIVE;
import static com.ost.walletsdk.models.entities.OstTokenHolder.CONST_STATUS.LOGGED_OUT;
import static com.ost.walletsdk.models.entities.OstTokenHolder.CONST_STATUS.LOGGING_OUT;

/**
 * To hold Token Holder info
 */
@Entity(tableName = "token_holder")
public class OstTokenHolder extends OstBaseEntity {

    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";

    public static String getIdentifier() {
        return OstTokenHolder.ADDRESS;
    }

    public static class CONST_STATUS {
        public static final String ACTIVE = "active";
        public static final String LOGGING_OUT = "logging out";
        public static final String LOGGED_OUT = "logged out";
    }

    public static boolean isValidStatus(String status) {
        return Arrays.asList(ACTIVE, LOGGING_OUT, LOGGED_OUT).contains(status);
    }

    public static OstTokenHolder getById(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        id = Keys.toChecksumAddress(id);
        return OstModelFactory.getTokenHolderModel().getEntityById(id);
    }

    private static EntityFactory entityFactory;
    private static EntityFactory getEntityFactory() {
        if ( null == entityFactory ) {
            entityFactory = new EntityFactory() {
                @Override
                public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                    return new OstTokenHolder(jsonObject);
                }
            };
        }
        return entityFactory;
    }

    public static OstTokenHolder parse(JSONObject jsonObject) throws JSONException {
        return (OstTokenHolder) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getTokenHolderModel(), getIdentifier(), getEntityFactory());
    }


    @Override
    protected OstTokenHolder updateWithJsonObject(JSONObject jsonObject) throws JSONException {
        return OstTokenHolder.parse(jsonObject);
    }

    public OstTokenHolder(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstTokenHolder(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstTokenHolder.USER_ID) &&
                jsonObject.has(OstTokenHolder.ADDRESS);


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


    public OstSession getDeviceTokenHolderSession() throws Exception {
        OstSession deviceSession = null;
        OstSession sessions[] = OstModelFactory.getSessionModel().getEntitiesByParentId(getId());
        for (OstSession session : sessions) {
            if (null != new OstSecureKeyModelRepository().getByKey(session.getAddress())) {
                deviceSession = session;
                break;
            }
        }
        if (null == deviceSession) {
            throw new Exception("Wallet not found in db");
        }
        return deviceSession;
    }

    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }

    @Override
    public String getParentIdKey() {
        return OstDevice.USER_ID;
    }
}
