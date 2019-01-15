package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "rule")
public class Rule extends BaseEntity {

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

    public Rule() {
    }

    public Rule(JSONObject jsonObject) throws JSONException {
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
        setName(jsonObject.getString(Rule.NAME));
        setTokenId(jsonObject.getString(Rule.TOKEN_ID));
        setAbi(jsonObject.getString(Rule.ABI));
        setAddress(jsonObject.getString(Rule.ADDRESS));
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(Rule.TOKEN_ID) &&
                jsonObject.has(Rule.NAME) &&
                jsonObject.has(Rule.ABI) &&
                jsonObject.has(Rule.ADDRESS);
    }
}
