package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;

import com.ost.ostsdk.models.Impls.OstModelFactory;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "token")
public class OstToken extends OstBaseEntity {
    public OstToken(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    private OstToken(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public OstToken() {
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject);
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
    }

    public OstRule initRule(JSONObject jsonObject) throws JSONException {
        jsonObject.put(OstBaseEntity.PARENT_ID, getId());
        return OstModelFactory.getRuleModel().initRule(jsonObject);
    }

    public OstRule getRule(String id) {
        return OstModelFactory.getRuleModel().getRuleById(id);
    }

    public void delRule(String id) {
        OstModelFactory.getRuleModel().deleteRule(id);
    }
}