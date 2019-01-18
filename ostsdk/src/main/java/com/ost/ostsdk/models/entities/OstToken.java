package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.models.Impls.OstModelFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "token")
public class OstToken extends OstBaseEntity {

    public static final String NAME = "name";
    public static final String SYMBOL = "symbol";
    public static final String CONVERSION_FACTOR = "conversion_factor";
    public static final String TOTAL_SUPPLY = "total_supply";
    public static final String ORIGIN_CHAIN = "origin_chain";
    public static final String AUXILIARY_CHAIN = "auxiliary_chain";


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
        return super.validate(jsonObject) &&
                jsonObject.has(OstToken.NAME) &&
                jsonObject.has(OstToken.SYMBOL) &&
                jsonObject.has(OstToken.CONVERSION_FACTOR) &&
                jsonObject.has(OstToken.TOTAL_SUPPLY) &&
                jsonObject.has(OstToken.ORIGIN_CHAIN) &&
                jsonObject.has(OstToken.AUXILIARY_CHAIN);
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

    public String getName() {
        return getData().optString(OstToken.NAME, null);
    }

    public String getSymbol() {
        return getData().optString(OstToken.SYMBOL, null);
    }

    public String getConversionFactor() {
        return getData().optString(OstToken.CONVERSION_FACTOR, null);
    }

    public String getTotalSupply() {
        return getData().optString(OstToken.TOTAL_SUPPLY, null);
    }

    public JSONObject getOriginChain() {
        return getData().optJSONObject(OstToken.ORIGIN_CHAIN);
    }

    public JSONArray getAuxiliaryChain() {
        return getData().optJSONArray(OstToken.AUXILIARY_CHAIN);
    }

    @Override
    String getEntityIdKey() {
        return OstUser.ID;
    }
}