package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.models.Impls.OstModelFactory;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "rule")
public class OstRule extends OstBaseEntity {

    public static final String TOKEN_ID = "token_id";
    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String ABI = "abi";
    public static final String CALL_PREFIX = "call_prefix";

    public static OstRule parse(JSONObject jsonObject) throws JSONException {
        OstRule ostRule = new OstRule(jsonObject);
        return OstModelFactory.getRuleModel().insert(ostRule);
    }

    public OstRule(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstRule(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public String getTokenId() {
        return getData().optString(OstRule.TOKEN_ID, null);
    }

    public String getAddress() {
        return getData().optString(OstRule.ADDRESS, null);
    }

    public String getAbi() {
        return getData().optString(OstRule.ABI, null);
    }

    public String getName() {
        return getData().optString(OstRule.NAME, null);
    }

    public String getCallPrefix() {
        return getData().optString(OstRule.CALL_PREFIX, null);
    }


    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstRule.TOKEN_ID) &&
                jsonObject.has(OstRule.NAME) &&
                jsonObject.has(OstRule.ABI) &&
                jsonObject.has(OstRule.CALL_PREFIX) &&
                jsonObject.has(OstRule.ADDRESS);
    }

    @Override
    String getEntityIdKey() {
        return OstUser.ID;
    }

    @Override
    public String getParentIdKey() {
        return OstUser.TOKEN_ID;
    }
}
