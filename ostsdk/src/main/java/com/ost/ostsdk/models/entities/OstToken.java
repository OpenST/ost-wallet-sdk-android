package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.models.Impls.OstModelFactory;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "token")
public class OstToken extends OstBaseEntity {

    public static OstToken parse(JSONObject jsonObject) throws JSONException {
        OstToken ostToken = new OstToken(jsonObject);
        return OstModelFactory.getTokenModel().insert(ostToken);
    }

    public OstToken(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstToken(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
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
        return OstModelFactory.getRuleModel().insert(OstRule.parse(jsonObject));
    }

    public OstRule getRule(String id) {
        return OstModelFactory.getRuleModel().getRuleById(id);
    }

    public void delRule(String id) {
        OstModelFactory.getRuleModel().deleteRule(id);
    }
}