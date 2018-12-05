package com.ost.ostsdk.data.models.entities;

import android.arch.persistence.room.Entity;

import org.json.JSONObject;

@Entity(tableName = "user")
public class UserEntity extends BaseEntity {

    private double economyId;

    private double tokenHolderId;

    private String name;

    public UserEntity() {

    }

    public double getEconomyId() {
        return economyId;
    }

    public double getTokenHolderId() {
        return tokenHolderId;
    }

    public String getName() {
        return name;
    }

    public void setEconomyId(double economyId) {
        this.economyId = economyId;
    }

    public void setTokenHolderId(double tokenHolderId) {
        this.tokenHolderId = tokenHolderId;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void processJson(JSONObject data) {
        super.processJson(data);

    }
}