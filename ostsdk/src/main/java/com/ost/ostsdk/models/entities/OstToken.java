package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.ost.ostsdk.models.Impls.OstModelFactory;
import com.ost.ostsdk.models.OstTaskCallback;

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

    public OstRule initRule(JSONObject jsonObject, @NonNull OstTaskCallback callback) throws JSONException {
        jsonObject.put(OstBaseEntity.PARENT_ID, getId());
        return OstModelFactory.getRuleModel().initRule(jsonObject, callback);
    }

    public OstRule initRule(JSONObject jsonObject) throws JSONException {
        return initRule(jsonObject, new OstTaskCallback() {
        });
    }

    public OstRule getRule(String id) {
        return OstModelFactory.getRuleModel().getRuleById(id);
    }

    public void delRule(String id, OstTaskCallback callback) {
        OstModelFactory.getRuleModel().deleteRule(id, callback);
    }

    public void delRule(String id) {
        delRule(id, new OstTaskCallback() {
        });
    }
}