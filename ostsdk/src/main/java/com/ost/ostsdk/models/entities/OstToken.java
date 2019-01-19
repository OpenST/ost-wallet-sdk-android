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


    public static String getIdentifier() {
        return OstUser.ID;
    }

    public static OstToken parse(JSONObject jsonObject) throws JSONException {
        return (OstToken) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getTokenModel(), getIdentifier(), new EntityFactory() {
            @Override
            public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                return new OstToken(jsonObject);
            }
        });
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
        return OstRule.parse(jsonObject);
    }

    public OstRule getRule(String id) {
        return OstModelFactory.getRuleModel().getEntityById(id);
    }

    public void delRule(String id) {
        OstModelFactory.getRuleModel().deleteEntity(id);
    }

    public String getName() {
        return getJSONData().optString(OstToken.NAME, null);
    }

    public String getSymbol() {
        return getJSONData().optString(OstToken.SYMBOL, null);
    }

    public String getConversionFactor() {
        return getJSONData().optString(OstToken.CONVERSION_FACTOR, null);
    }

    public String getTotalSupply() {
        return getJSONData().optString(OstToken.TOTAL_SUPPLY, null);
    }

    public JSONObject getOriginChain() {
        return getJSONData().optJSONObject(OstToken.ORIGIN_CHAIN);
    }

    public JSONArray getAuxiliaryChain() {
        return getJSONData().optJSONArray(OstToken.AUXILIARY_CHAIN);
    }

    @Override
    String getEntityIdKey() {
        return OstUser.ID;
    }
}