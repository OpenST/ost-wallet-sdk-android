package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "user")
public class User extends BaseEntity {

    public static final String ECONOMY_ID = "economy_id";
    public static final String TOKEN_HOLDER_ID = "token_holder_id";
    public static final String NAME = "name";

    private double economyId;

    private double tokenHolderId;

    private String name;

    public User() {
    }

    public User(JSONObject jsonObject) {
        super(jsonObject);
    }

    private User(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
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

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(User.ECONOMY_ID) &&
                jsonObject.has(User.TOKEN_HOLDER_ID) &&
                jsonObject.has(User.NAME);
    }
}