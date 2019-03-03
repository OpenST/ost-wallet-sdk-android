package com.ost.mobilesdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.mobilesdk.models.Impls.OstModelFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Keys;

@Entity(tableName = "rule")
public class OstRule extends OstBaseEntity {

    public static final String TOKEN_ID = "token_id";
    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String ABI = "abi";
    public static final String CALL_PREFIX = "call_prefix";

    public static String getIdentifier() {
        return OstRule.NAME;
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
        return this.getJsonDataPropertyAsString(OstRule.TOKEN_ID);
    }

    public String getAddress() {
        String address = this.getJsonDataPropertyAsString(OstRule.ADDRESS);
        if (null != address) {
            address = Keys.toChecksumAddress(address);
        }
        return address;
    }

    public String getAbi() {
        return this.getJsonDataPropertyAsString(OstRule.ABI);
    }

    public String getName() {
        return this.getJsonDataPropertyAsString(OstRule.NAME);
    }

    public String getCallPrefix() {
        //To-Do: Call Prefix is an array.
        //But, do we really need it.
        return this.getJsonDataPropertyAsString(OstRule.CALL_PREFIX);
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
