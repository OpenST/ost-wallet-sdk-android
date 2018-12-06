package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "multi_sig_operation")
public class MultiSigOperation extends BaseEntity {

    public static final String STATUS = "status";
    public static final String LOCAL_ENTITY_ID = "local_entity_id";
    public static final String USER_ID = "user_id";
    public static final String TOKEN_HOLDER_ADDRESS = "token_holder_address";
    public static final String KIND = "kind";
    public static final String ENCODED_DATA = "encoded_data";
    public static final String RAW_DATA = "raw_data";
    public static final String SIGNATURES = "signatures";

    private double userId;
    private double localEntityId;
    private String tokenHolderAddress;
    private String kind;
    private String encodedData;
    private JSONObject rawData;
    private JSONObject signatures;
    private String status;


    public MultiSigOperation(JSONObject jsonObject) {
        super(jsonObject);
    }

    private MultiSigOperation(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(MultiSigOperation.USER_ID) &&
                jsonObject.has(MultiSigOperation.STATUS) &&
                jsonObject.has(MultiSigOperation.LOCAL_ENTITY_ID) &&
                jsonObject.has(MultiSigOperation.TOKEN_HOLDER_ADDRESS) &&
                jsonObject.has(MultiSigOperation.KIND) &&
                jsonObject.has(MultiSigOperation.ENCODED_DATA) &&
                jsonObject.has(MultiSigOperation.SIGNATURES) &&
                jsonObject.has(MultiSigOperation.RAW_DATA);


    }

    @Override
    public void processJson(JSONObject jsonObject) {
        super.processJson(jsonObject);
    }

    public double getUserId() {
        return userId;
    }

    public void setUserId(double userId) {
        this.userId = userId;
    }

    public double getLocalEntityId() {
        return localEntityId;
    }

    public void setLocalEntityId(double localEntityId) {
        this.localEntityId = localEntityId;
    }

    public String getTokenHolderAddress() {
        return tokenHolderAddress;
    }

    public void setTokenHolderAddress(String tokenHolderAddress) {
        this.tokenHolderAddress = tokenHolderAddress;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEncodedData() {
        return encodedData;
    }

    public void setEncodedData(String encodedData) {
        this.encodedData = encodedData;
    }

    public JSONObject getRawData() {
        return rawData;
    }

    public void setRawData(JSONObject rawData) {
        this.rawData = rawData;
    }

    public JSONObject getSignatures() {
        return signatures;
    }

    public void setSignatures(JSONObject signatures) {
        this.signatures = signatures;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }
}
