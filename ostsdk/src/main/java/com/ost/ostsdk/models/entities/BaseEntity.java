package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.json.JSONObject;

public class BaseEntity {

    public static final String ID = "id";
    public static final String PARENT_ID = "parent_id";
    public static final String JSON_DATA = "data";
    public static final String STATUS = "baseStatus";
    public static final String UTS = "uts";
    public static final String LOCAL_ENTITY_ID = "local_entity_id";

    public static final String ACTIVE_STATUS = "ACTIVE";
    public static final String DELETED_STATUS = "DELETED";
    private static final String DEFAULT_PARENT_ID = "";

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


    BaseEntity() {
    }

    BaseEntity(JSONObject jsonObject) {
        if (!validate(jsonObject)) {
            throw new RuntimeException("Invalid JSON Object");
        }
        processJson(jsonObject);
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
        return jsonObject.has(BaseEntity.ID);
    }

    public void processJson(JSONObject jsonObject) {
        try {
            setId(jsonObject.getString(BaseEntity.ID));
            setUts(jsonObject.optDouble(BaseEntity.UTS, -1 * System.currentTimeMillis()));
            setBaseStatus(jsonObject.optString(BaseEntity.STATUS, BaseEntity.ACTIVE_STATUS));
            setParentId(jsonObject.optString(BaseEntity.PARENT_ID, BaseEntity.DEFAULT_PARENT_ID));
            setData(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
