package com.ost.mobilesdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.util.Log;

import com.ost.mobilesdk.models.Impls.OstModelFactory;

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
    public static final String AUXILIARY_CHAINS = "auxiliary_chains";
    private static final String CHAIN_ID = "chain_id";
    private static final String TAG = "OstToken";


    public static String getIdentifier() {
        return OstToken.ID;
    }

    private static EntityFactory entityFactory;
    private static EntityFactory getEntityFactory() {
        if ( null == entityFactory ) {
            entityFactory = new EntityFactory() {
                @Override
                public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                    return new OstToken(jsonObject);
                }
            };
        }
        return entityFactory;
    }

    public static OstToken parse(JSONObject jsonObject) throws JSONException {
        return (OstToken) OstBaseEntity.insertOrUpdate(jsonObject, OstModelFactory.getTokenModel(), getIdentifier(), getEntityFactory());
    }

    @Override
    protected OstToken updateWithJsonObject(JSONObject jsonObject) throws JSONException {
        return OstToken.parse(jsonObject);
    }

    public OstToken(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstToken(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public static OstToken getById(String tokenId) {
        return OstModelFactory.getTokenModel().getEntityById(tokenId);
    }

    public static OstToken init(String tokenId) {
        OstToken ostToken = OstToken.getById(tokenId);
        if (null != ostToken) {
            Log.d(TAG, String.format("OstToken with Id %s already exists", tokenId));
            return ostToken;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(OstToken.ID, tokenId);
            jsonObject.put(OstToken.NAME, "");
            jsonObject.put(OstToken.SYMBOL, "");
            jsonObject.put(OstToken.CONVERSION_FACTOR, "");
            jsonObject.put(OstToken.TOTAL_SUPPLY, "");
            jsonObject.put(OstToken.ORIGIN_CHAIN, "");
            jsonObject.put(OstToken.AUXILIARY_CHAINS, "");
            return OstToken.parse(jsonObject);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected error: OstToken json updateWithApiResponse exception");
        }
        return null;
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstToken.NAME) &&
                jsonObject.has(OstToken.SYMBOL) &&
                jsonObject.has(OstToken.CONVERSION_FACTOR) &&
                jsonObject.has(OstToken.TOTAL_SUPPLY);
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
        return this.getJsonDataPropertyAsString(OstToken.NAME);
    }

    public String getSymbol() {
        return this.getJsonDataPropertyAsString(OstToken.SYMBOL);
    }

    public String getConversionFactor() {
        return this.getJsonDataPropertyAsString(OstToken.CONVERSION_FACTOR);
    }

    public String getTotalSupply() {
        return this.getJsonDataPropertyAsString(OstToken.TOTAL_SUPPLY);
    }

    public JSONObject getOriginChain() {
        //To-Do: Make it safe.
        return getJSONData().optJSONObject(OstToken.ORIGIN_CHAIN);
    }

    public JSONArray getAuxiliaryChain() {
        //To-Do: Make it safe.
        return getJSONData().optJSONArray(OstToken.AUXILIARY_CHAINS);
    }

    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }

    public String getChainId() {
        try {
            JSONArray jsonArray = getAuxiliaryChain();
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String chainId = jsonObject.getString(CHAIN_ID);
            return chainId;
        } catch (Exception e) {
            Log.e(TAG, "Exception while getting chainId", e);
            return null;
        }
    }

    @Override
    public String getParentId() {
        return "";
    }

    @Override
    public String getStatus() {
        return "";
    }

    public OstRule[] getAllRules() {
        return OstModelFactory.getRuleModel().getEntitiesByParentId(getId());
    }
}