package com.ost.ostsdk.data.models.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONObject;

public class BaseEntity {

    public static final String ID = "id";
    public static final String PARENT_ID = "parent_id";
    public static final String JSON_DATA = "data";
    public static final String STATUS = "status";
    public static final String UTS = "uts";

    @PrimaryKey()
    @ColumnInfo(name = "id")
    private double id;

    @ColumnInfo(name = "data")
    private String data;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "uts")
    private double uts;

    BaseEntity(){

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

    public void processJson(JSONObject data) {
        if (data != null && data.length() > 0) {
            try {
                if ( data.has(BaseEntity.ID) ) {
                    this.id = data.getDouble(BaseEntity.ID);
                } else {

                }
                this.uts = data.optDouble(BaseEntity.UTS, -1 * System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if ( null == data ) {
            } else {
            }
        }

    }

    public void generateLocalUts() {
        this.uts = -1 * System.currentTimeMillis();
    }
}
