package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "rule")
public class OstRule extends OstBaseEntity {

    public static final String TOKEN_ID = "token_id";
    public static final String ADDRESS = "address";
    public static final String NAME = "name";
    public static final String ABI = "abi";

    @Ignore
    private String tokenId;
    @Ignore
    private String name;
    @Ignore
    private String address;
    @Ignore
    private String abi;


    public OstRule(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstRule(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public String getTokenId() {
        return tokenId;
    }

    public String getAddress() {
        return address;
    }

    public String getAbi() {
        return abi;
    }

    public String getName() {
        return name;
    }


    private void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    private void setAbi(String abi) {
        this.abi = abi;
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
        setName(jsonObject.getString(OstRule.NAME));
        setTokenId(jsonObject.getString(OstRule.TOKEN_ID));
        setAbi(jsonObject.getString(OstRule.ABI));
        setAddress(jsonObject.getString(OstRule.ADDRESS));
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstRule.TOKEN_ID) &&
                jsonObject.has(OstRule.NAME) &&
                jsonObject.has(OstRule.ABI) &&
                jsonObject.has(OstRule.ADDRESS);
    }
}
