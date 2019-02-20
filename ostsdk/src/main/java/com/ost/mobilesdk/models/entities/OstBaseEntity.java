package com.ost.mobilesdk.models.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ost.mobilesdk.models.OstBaseModel;

import org.json.JSONException;
import org.json.JSONObject;

public class OstBaseEntity {

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
            Log.e(TAG, "Failed to parse JSON fetched from DB");
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
        OstBaseEntity dbEntity = ostBaseModel.getEntityById(identifier);
        if (null != dbEntity) {
            if (dbEntity.getUpdatedTimestamp() != OstBaseEntity.getUpdatedTimestamp(jsonObject)) {
                return (OstUser) dbEntity;
            }
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
            return jsonObject.getString(getEntityIdKey());
        } catch (JSONException e) {
            Log.e(TAG, "Failed to read id from jsonObject");
            return null;
        }
    }

    public double getUpdatedTimestamp() {
        JSONObject jsonObject = this.getJSONData();
        if ( null == jsonObject ) {
            Log.e(TAG, "jsonObject is null");
            return -1 * System.currentTimeMillis();
        }

        try {
            return jsonObject.getDouble(OstBaseEntity.UPDATED_TIMESTAMP);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to read timestamp from jsonObject");
            return -1 * System.currentTimeMillis();
        }
    }

    public void updateTimestamp() {
        JSONObject jsonObject = this.getJSONData();
        try {
            jsonObject.put(OstBaseEntity.UPDATED_TIMESTAMP, -1 * System.currentTimeMillis());
        } catch (JSONException e) {
            Log.e(TAG, "Failed to update timestamp in jsonObject");
        }
    }

    public String getParentId() {
        JSONObject jsonObject = this.getJSONData();
        if ( null == jsonObject ) {
            Log.e(TAG, "jsonObject is null");
            return null;
        }
        String parentIdKey = getParentIdKey();
        try {
            return jsonObject.getString(parentIdKey);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to read parent-id from jsonObject. parentIdKey = " + parentIdKey);
            return null;
        }
    }

    public String getStatus() {
        JSONObject jsonObject = this.getJSONData();
        if ( null == jsonObject ) {
            Log.e(TAG, "jsonObject is null");
            return null;
        }

        try {
            return jsonObject.getString(OstBaseEntity.STATUS);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to read status from jsonObject.");
            return null;
        }
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

    interface EntityFactory {
        OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException;
    }
}
