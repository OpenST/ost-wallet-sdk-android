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

    private static final String DEFAULT_PARENT_ID = "";
    private static final String TAG = "OstBaseEntity";

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "id")
    private String id = "";

    @ColumnInfo(name = "parent_id")
    private String parentId = DEFAULT_PARENT_ID;

    @ColumnInfo(name = "data")
    private JSONObject data;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "uts")
    private double updatedTimestamp;


    public OstBaseEntity(@NonNull String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        this.id = id;
        this.parentId = parentId;
        this.data = data;
        this.status = status;
        this.updatedTimestamp = updatedTimestamp;
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
            if (dbEntity.getUpdatedTimestamp() >= OstBaseEntity.getUpdatedTimestamp(jsonObject)) {
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

    public String getParentId() {
        return parentId;
    }

    public String getId() {
        return id;
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

    public String getStatus() {
        return status;
    }

    public double getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    private void setId(@NonNull String id) {
        this.id = id;
    }

    private void setData(JSONObject data) {
        this.data = data;
    }

    private void setStatus(String status) {
        this.status = status;
    }

    private void setParentId(String parentId) {
        this.parentId = parentId;
    }

    private void setUpdatedTimestamp(double updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    boolean validate(JSONObject jsonObject) {
        return true;
//        return jsonObject.has(OstBaseEntity.ID);
    }

    public void processJson(JSONObject jsonObject) throws JSONException {
        String id = jsonObject.getString(getEntityIdKey());
        if (id.length() <= 1) {
            throw new JSONException("Id should be more than 1 characters long");
        }

        setId(id);

        setUpdatedTimestamp(jsonObject.optDouble(OstBaseEntity.UPDATED_TIMESTAMP, -1 * System.currentTimeMillis()));

        String parentId = jsonObject.optString(getParentIdKey(), OstBaseEntity.DEFAULT_PARENT_ID);
        setParentId(parentId);

        setStatus(jsonObject.optString(OstBaseEntity.STATUS, getDefaultStatus()));
        setData(jsonObject);
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
