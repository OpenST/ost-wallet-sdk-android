package com.ost.mobilesdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.mobilesdk.models.Impls.OstModelFactory;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "rule")
public class OstRule extends OstBaseEntity {

    public static final String TOKEN_ID = "token_id";
    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String ABI = "abi";
    public static final String CALL_PREFIX = "call_prefix";

    public static String getIdentifier() {
        return OstRule.ID;
    }

    private static EntityFactory entityFactory;
    private static EntityFactory getEntityFactory() {
        if ( null == entityFactory ) {
            entityFactory = new EntityFactory() {
                @Override
                public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                    return new OstRule(jsonObject);
                }
            };
        }
        return entityFactory;
    }

    public static OstRule parse(JSONObject jsonObject) throws JSONException {
        return (OstRule) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getRuleModel(), getIdentifier(), getEntityFactory());
    }

    @Override
    protected OstRule updateWithJsonObject(JSONObject jsonObject) throws JSONException {
        return OstRule.parse(jsonObject);
    }


    public OstRule(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstRule(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public String getTokenId() {
        return getJSONData().optString(OstRule.TOKEN_ID, null);
    }

    public String getAddress() {
        return getJSONData().optString(OstRule.ADDRESS, null);
    }

    public String getAbi() {
        return getJSONData().optString(OstRule.ABI, null);
    }

    public String getName() {
        return getJSONData().optString(OstRule.NAME, null);
    }

    public String getCallPrefix() {
        return getJSONData().optString(OstRule.CALL_PREFIX, null);
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
        return getIdentifier();
    }

    @Override
    public String getParentIdKey() {
        return OstRule.TOKEN_ID;
    }
}
