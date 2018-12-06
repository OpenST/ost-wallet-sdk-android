package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "token_holder_wallet")
public class TokenHolderWallet extends BaseEntity {

    public static final String STATUS = "status";
    public static final String ADDRESS = "address";
    public static final String LOCAL_ENTITY_ID = "local_entity_id";
    public static final String TOKEN_HOLDER_ID = "token_holder_id";

    @Ignore
    private String status;
    @Ignore
    private double tokenHolderId;
    @Ignore
    private String address;
    @Ignore
    private double localEntityId;

    public TokenHolderWallet(JSONObject jsonObject) {
        super(jsonObject);
    }

    private TokenHolderWallet(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public TokenHolderWallet() {
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(TokenHolderWallet.LOCAL_ENTITY_ID) &&
                jsonObject.has(TokenHolderWallet.ADDRESS) &&
                jsonObject.has(TokenHolderWallet.STATUS) &&
                jsonObject.has(TokenHolderWallet.TOKEN_HOLDER_ID);
    }

    @Override
    public void processJson(JSONObject jsonObject) {
        super.processJson(jsonObject);
    }


    public String getAddress() {
        return address;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public double getTokenHolderId() {
        return tokenHolderId;
    }

    public double getLocalEntityId() {
        return localEntityId;
    }

    private void setStatus(String status) {
        this.status = status;
    }

    private void setTokenHolderId(double tokenHolderId) {
        this.tokenHolderId = tokenHolderId;
    }

    private void setLocalEntityId(double localEntityId) {
        this.localEntityId = localEntityId;
    }
}
