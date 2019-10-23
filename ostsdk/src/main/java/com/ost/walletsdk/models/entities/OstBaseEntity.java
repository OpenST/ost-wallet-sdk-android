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

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import com.ost.walletsdk.annotations.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.ost.walletsdk.models.OstBaseModel;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class OstBaseEntity {

    public static final String ID = "id";
    public static final String PARENT_ID = "parent_id";
    public static final String JSON_DATA = "data";
    public static final String STATUS = "status";
    public static final String UPDATED_TIMESTAMP = "updated_timestamp";

    static final String DEFAULT_PARENT_ID_KEY = "";
    private static final String TAG = "OstBaseEntity";

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "id")
    private String id = "";

    @ColumnInfo(name = "parent_id")
    private String parentId = DEFAULT_PARENT_ID_KEY;

    @ColumnInfo(name = "data")
    private JSONObject data;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "uts")
    private double updatedTimestamp;


    public OstBaseEntity(@NonNull String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        try {
            this.processJson(data);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to updateWithApiResponse JSON fetched from DB");
        }
    }

    @Ignore
    OstBaseEntity(JSONObject jsonObject) throws JSONException {
        if (!validate(jsonObject)) {
            throw new JSONException("Invalid JSON Object");
        }
        processJson(jsonObject);
    }

    public static OstBaseEntity insertOrUpdate(JSONObject jsonObject, OstBaseModel ostBaseModel, String identifier, EntityFactory entityFactory) throws JSONException {
        String id = jsonObject.optString(identifier);
        if (TextUtils.isEmpty(id)) {
            throw new JSONException("Identifier value is null");
        }
        OstBaseEntity dbEntity = ostBaseModel.getEntityById(id);
        if (null != dbEntity) {
            dbEntity.processJson(jsonObject);
        } else {
            dbEntity = entityFactory.createEntity(jsonObject);
        }
        ostBaseModel.insertOrUpdateEntity(dbEntity);
        return dbEntity;
    }

    public static double getUpdatedTimestamp(JSONObject jsonObject) {
        return jsonObject.optDouble(OstBaseEntity.UPDATED_TIMESTAMP, Double.MIN_VALUE);
    }





    public JSONObject getData() {
        try {
            return new JSONObject(data.toString());
        } catch (Exception e) {
            Log.e(TAG, "JSON Parsing error");
        }
        return new JSONObject();
    }

    JSONObject getJSONData() {
        return data;
    }





    boolean validate(JSONObject jsonObject) {
        return true;
    }

    public void processJson(JSONObject jsonObject) throws JSONException {
        //Update Timestamp if needed.
        if ( !jsonObject.has(OstBaseEntity.UPDATED_TIMESTAMP) ) {
            jsonObject.put(OstBaseEntity.UPDATED_TIMESTAMP, -1 * System.currentTimeMillis());
        }

        //Set Data.
        this.data = jsonObject;
    }

    public String getId() {
        JSONObject jsonObject = this.getJSONData();
        if ( null == jsonObject ) {
            Log.e(TAG, "jsonObject is null");
            return null;
        }
        try {
            String entityId = getEntityIdKey();
            if ( jsonObject.isNull(entityId) ) {
                Log.e(TAG, "Failed to read id from jsonObject" + ". Entity = " + this.getClass().toString());
                return null;
            }
            return jsonObject.getString(entityId);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to read id from jsonObject" + ". Entity = " + this.getClass().toString());
            return null;
        }
    }

    public double getUpdatedTimestamp() {
        JSONObject jsonObject = this.getJSONData();
        if ( null == jsonObject ) {
            Log.e(TAG, "jsonObject is null");
            return -1 * System.currentTimeMillis();
        }

        String jsonKey = OstBaseEntity.UPDATED_TIMESTAMP;
        if ( jsonObject.isNull(jsonKey) ) {
            Log.d(TAG, "Failed to read timestamp from jsonObject" + ". Entity = " + this.getClass().toString());
            return -1 * System.currentTimeMillis();
        }

        try {
            return jsonObject.getDouble(jsonKey);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to read timestamp from jsonObject" + ". Entity = " + this.getClass().toString());
            return -1 * System.currentTimeMillis();
        }
    }

    public void updateTimestamp() {
        JSONObject jsonObject = this.getJSONData();
        try {
            jsonObject.put(OstBaseEntity.UPDATED_TIMESTAMP, -1 * System.currentTimeMillis());
        } catch (JSONException e) {
            Log.e(TAG, "Failed to update timestamp in jsonObject" + ". Entity = " + this.getClass().toString());
        }
    }

    public String getParentId() {
        JSONObject jsonObject = this.getJSONData();
        if ( null == jsonObject ) {
            Log.e(TAG, "jsonObject is null");
            return null;
        }
        String parentIdKey = getParentIdKey();
        if ( jsonObject.isNull(parentIdKey) ) {
            Log.e(TAG, "Failed to read parent-id from jsonObject. parentIdKey = " + parentIdKey+ ". Entity = " + this.getClass().toString());
            return null;
        }

        try {
            return jsonObject.getString(parentIdKey);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to read parent-id from jsonObject. parentIdKey = " + parentIdKey+ ". Entity = " + this.getClass().toString());
            return null;
        }
    }

    public String getStatus() {
        JSONObject jsonObject = this.getJSONData();
        if ( null == jsonObject ) {
            Log.e(TAG, "jsonObject is null");
            return null;
        }
        return jsonObject.optString(OstBaseEntity.STATUS, null);
    }


    public String getDefaultStatus() {
        return "";
    }

    String getEntityIdKey() {
        return OstBaseEntity.ID;
    }

    String getParentIdKey() {
        return OstBaseEntity.PARENT_ID;
    }


    abstract protected  OstBaseEntity updateWithJsonObject(JSONObject jsonObject) throws JSONException;

    protected void setJsonDataProperty(String key, Object obj) throws JSONException {
        //Create a copy of JSON Data.
        JSONObject jsonObjectCopy = this.getData();
        if ( null == jsonObjectCopy ) {
            //Make sure jsonObject Copy is present.
            throw new JSONException("The entity does not have jsonObject");
        }

        //Now set the key/value
        jsonObjectCopy.put(key, obj.toString() );

        //Update the timestamp
        jsonObjectCopy.put(OstBaseEntity.UPDATED_TIMESTAMP, -1 * System.currentTimeMillis());

        //Schedule DB Update.
        this.updateWithJsonObject(jsonObjectCopy);
    }

    protected String getJsonDataPropertyAsString(String key) {
        JSONObject jsonObject = this.getJSONData();
        if ( null == jsonObject ) {
            Log.e(TAG, "getJsonDataPropertyAsString: jsonObject is null. key = " + key + ". Entity = " + this.getClass().toString() );
            return null;
        }
        if ( jsonObject.isNull(key) ) {
            return null;
        }
        return jsonObject.optString(key,null);
    }

    interface EntityFactory {
        OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException;
    }
}
