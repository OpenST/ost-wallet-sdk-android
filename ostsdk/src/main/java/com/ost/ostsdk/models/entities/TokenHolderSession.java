package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "token_holder_session")
public class TokenHolderSession extends BaseEntity {

    public static final String STATUS = "status";
    public static final String ADDRESS = "address";
    public static final String LOCAL_ENTITY_ID = "local_entity_id";
    public static final String TOKEN_HOLDER_ID = "token_holder_id";
    public static final String BLOCK_HEIGHT = "block_height";
    public static final String EXPIRY_TIME = "expiry_time";
    public static final String SPENDING_LIMIT = "spending_limit";
    public static final String REDEMPTION_LIMIT = "redemption_limit";


    private String status;
    private String address;
    private double localEntityId;
    private double tokenHolderId;
    private double blockHeight;
    private double expiryTime;
    private double spendingLimit;
    private double redemptionLimit;


    public TokenHolderSession(JSONObject jsonObject) {
        super(jsonObject);
    }

    private TokenHolderSession(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(TokenHolderSession.STATUS) &&
                jsonObject.has(TokenHolderSession.ADDRESS) &&
                jsonObject.has(TokenHolderSession.LOCAL_ENTITY_ID) &&
                jsonObject.has(TokenHolderSession.TOKEN_HOLDER_ID) &&
                jsonObject.has(TokenHolderSession.BLOCK_HEIGHT) &&
                jsonObject.has(TokenHolderSession.EXPIRY_TIME) &&
                jsonObject.has(TokenHolderSession.REDEMPTION_LIMIT) &&
                jsonObject.has(TokenHolderSession.SPENDING_LIMIT);
    }

    @Override
    public void processJson(JSONObject jsonObject) {
        super.processJson(jsonObject);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public double getLocalEntityId() {
        return localEntityId;
    }

    public void setLocalEntityId(double localEntityId) {
        this.localEntityId = localEntityId;
    }

    public double getTokenHolderId() {
        return tokenHolderId;
    }

    public void setTokenHolderId(double tokenHolderId) {
        this.tokenHolderId = tokenHolderId;
    }

    public double getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(double blockHeight) {
        this.blockHeight = blockHeight;
    }

    public double getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(double expiryTime) {
        this.expiryTime = expiryTime;
    }

    public double getSpendingLimit() {
        return spendingLimit;
    }

    public void setSpendingLimit(double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public double getRedemptionLimit() {
        return redemptionLimit;
    }

    public void setRedemptionLimit(double redemptionLimit) {
        this.redemptionLimit = redemptionLimit;
    }
}
