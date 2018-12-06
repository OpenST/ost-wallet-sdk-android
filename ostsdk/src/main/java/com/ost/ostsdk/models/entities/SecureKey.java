package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "secure_key")
public class SecureKey extends BaseEntity {
    public SecureKey(JSONObject jsonObject) {
        super(jsonObject);
    }

    private SecureKey(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public SecureKey() {
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
