package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "economy")
public class Economy extends BaseEntity {
    public Economy(JSONObject jsonObject) {
        super(jsonObject);
    }

    private Economy(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public Economy() {
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject);
    }

    @Override
    public void processJson(JSONObject jsonObject) {
        super.processJson(jsonObject);
    }
}
