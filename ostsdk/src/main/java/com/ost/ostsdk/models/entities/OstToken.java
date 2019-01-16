package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.ost.ostsdk.models.Impls.ModelFactory;
import com.ost.ostsdk.models.TaskCallback;

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

    public OstRule initRule(JSONObject jsonObject, @NonNull TaskCallback callback) throws JSONException {
        jsonObject.put(OstBaseEntity.PARENT_ID, getId());
        return ModelFactory.getRuleModel().initRule(jsonObject, callback);
    }

    public OstRule initRule(JSONObject jsonObject) throws JSONException {
        return initRule(jsonObject, new TaskCallback() {
        });
    }

    public OstRule getRule(String id) {
        return ModelFactory.getRuleModel().getRuleById(id);
    }

    public void delRule(String id, TaskCallback callback) {
        ModelFactory.getRuleModel().deleteRule(id, callback);
    }

    public void delRule(String id) {
        delRule(id, new TaskCallback() {
        });
    }
}