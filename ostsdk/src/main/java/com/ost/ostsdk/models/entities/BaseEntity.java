package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONObject;

public class BaseEntity {

    public static final String ID = "id";
    public static final String PARENT_ID = "parent_id";
    public static final String JSON_DATA = "data";
    public static final String STATUS = "status";
    public static final String UTS = "uts";

    public static final String ACTIVE_STATUS = "ACTIVE";
    public static final String DELETED_STATUS = "DELETED";

    @PrimaryKey()
    @ColumnInfo(name = "id")
    private double id;

    @ColumnInfo(name = "parent_id")
    private double parentId;

    @ColumnInfo(name = "data")
    private String data;

    @ColumnInfo(name = "status")
    private String status = ACTIVE_STATUS;

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

    public double getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public double getUts() {
        return uts;
    }

    public void setId(double id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUts(double uts) {
        this.uts = uts;
    }

    boolean validate(JSONObject jsonObject) {
        return jsonObject.has(BaseEntity.ID);
    }

    public void processJson(JSONObject jsonObject) {
        if (jsonObject != null && jsonObject.length() > 0) {
            try {
                if (jsonObject.has(BaseEntity.ID)) {
                    this.id = jsonObject.getDouble(BaseEntity.ID);
                } else {

                }
                this.uts = jsonObject.optDouble(BaseEntity.UTS, -1 * System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (null == jsonObject) {
            } else {
            }
        }

    }

    public void generateLocalUts() {
        this.uts = -1 * System.currentTimeMillis();
    }

    public double getParentId() {
        return parentId;
    }

    public void setParentId(double parentId) {
        this.parentId = parentId;
    }
}
