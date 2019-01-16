package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class OstBaseEntity {

    public static final String ID = "id";
    public static final String PARENT_ID = "parent_id";
    public static final String JSON_DATA = "data";
    public static final String STATUS = "baseStatus";
    public static final String UTS = "uts";
    public static final String LOCAL_ENTITY_ID = "local_entity_id";

    public static final String ACTIVE_STATUS = "ACTIVE";
    public static final String DELETED_STATUS = "DELETED";
    private static final String DEFAULT_PARENT_ID = "";
    private static final List<String> STATUS_VALUE = Arrays.asList(ACTIVE_STATUS, DELETED_STATUS);
    private static final String TAG = "OstBaseEntity";

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "id")
    private String id = "";

    @ColumnInfo(name = "parent_id")
    private String parentId = DEFAULT_PARENT_ID;

    @ColumnInfo(name = "data")
    private String data;

    @ColumnInfo(name = "baseStatus")
    private String baseStatus = ACTIVE_STATUS;

    @ColumnInfo(name = "uts")
    private double uts;


    OstBaseEntity() {
    }

    OstBaseEntity(JSONObject jsonObject) throws JSONException {
        if (!validate(jsonObject)) {
            throw new JSONException("Invalid JSON Object");
        }
        processJson(jsonObject);
        //Update in DB here. Use model as needed.
    }

    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public String getBaseStatus() {
        return baseStatus;
    }

    public double getUts() {
        return uts;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setBaseStatus(String status) {
        this.baseStatus = status;
    }

    public void setUts(double uts) {
        this.uts = uts;
    }

    boolean validate(JSONObject jsonObject) {
        return jsonObject.has(OstBaseEntity.ID);
    }

    public void processJson(JSONObject jsonObject) throws JSONException {
        //Todo: getENtityId
        String id = jsonObject.getString(OstBaseEntity.ID);
        if (!id.matches("[a-zA-Z0-9]+")) {
            throw new JSONException("Id having special characters in it");
        }
        setId(id);

        setUts(jsonObject.optDouble(OstBaseEntity.UTS, -1 * System.currentTimeMillis()));

        //Not needed
//        String status = jsonObject.optString(OstBaseEntity.STATUS, OstBaseEntity.ACTIVE_STATUS);
//        if (!OstBaseEntity.DEFAULT_PARENT_ID.equals(status) && !STATUS_VALUE.contains(status)) {
//            throw new JSONException("status having invalid value");
//        }
//        setBaseStatus(status);

        //Todo:: getParentId
        String parentId = jsonObject.optString(OstBaseEntity.PARENT_ID, OstBaseEntity.DEFAULT_PARENT_ID);
        if (!OstBaseEntity.DEFAULT_PARENT_ID.equals(parentId) && !parentId.matches("[a-zA-Z0-9]+")) {
            throw new JSONException("Parent Id having special characters in it");
        }
        setParentId(parentId);

        setData(jsonObject.toString());
    }

    public void generateLocalUts() {
        this.uts = -1 * System.currentTimeMillis();
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void updateJSON() {
        try {
            JSONObject jsonObject = new JSONObject(getData());
            jsonObject.put(OstBaseEntity.UTS, getUts());
            jsonObject.put(OstBaseEntity.PARENT_ID, getParentId());
            jsonObject.put(OstBaseEntity.STATUS, getBaseStatus());
            setData(jsonObject.toString());
        } catch (JSONException jsonException) {
            Log.e(TAG, "Unexpected exception while parsing JSON String");
            throw new RuntimeException("Unexpected exception while parsing JSON String");
        }
    }
}
