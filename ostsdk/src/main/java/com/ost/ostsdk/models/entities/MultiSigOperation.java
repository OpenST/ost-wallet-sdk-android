package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "multi_sig_operation")
public class MultiSigOperation extends BaseEntity {

    public static final String STATUS = "status";
    public static final String USER_ID = "user_id";
    public static final String TOKEN_HOLDER_ADDRESS = "token_holder_address";
    public static final String KIND = "kind";
    public static final String ENCODED_DATA = "encoded_data";
    public static final String RAW_DATA = "raw_data";
    public static final String SIGNATURES = "signatures";

    @Ignore
    private String userId;
    @Ignore
    private String tokenHolderAddress;
    @Ignore
    private String kind;
    @Ignore
    private String encodedData;
    @Ignore
    private JSONObject rawData;
    @Ignore
    private JSONObject signatures;
    @Ignore
    private String status;


    public MultiSigOperation(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    private MultiSigOperation(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public MultiSigOperation() {
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(MultiSigOperation.USER_ID) &&
                jsonObject.has(MultiSigOperation.STATUS) &&
                jsonObject.has(MultiSigOperation.TOKEN_HOLDER_ADDRESS) &&
                jsonObject.has(MultiSigOperation.KIND) &&
                jsonObject.has(MultiSigOperation.ENCODED_DATA) &&
                jsonObject.has(MultiSigOperation.SIGNATURES) &&
                jsonObject.has(MultiSigOperation.RAW_DATA);


    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
        setUserId(jsonObject.getString(MultiSigOperation.USER_ID));
        setStatus(jsonObject.getString(MultiSigOperation.STATUS));
        setTokenHolderAddress(jsonObject.getString(MultiSigOperation.TOKEN_HOLDER_ADDRESS));
        setKind(jsonObject.getString(MultiSigOperation.KIND));
        setEncodedData(jsonObject.getString(MultiSigOperation.ENCODED_DATA));
        setSignatures(jsonObject.getJSONObject(MultiSigOperation.SIGNATURES));
        setRawData(jsonObject.getJSONObject(MultiSigOperation.RAW_DATA));
    }

    public String getUserId() {
        return userId;
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTokenHolderAddress() {
        return tokenHolderAddress;
    }

    private void setTokenHolderAddress(String tokenHolderAddress) {
        this.tokenHolderAddress = tokenHolderAddress;
    }

    public String getKind() {
        return kind;
    }

    private void setKind(String kind) {
        this.kind = kind;
    }

    public String getEncodedData() {
        return encodedData;
    }

    private void setEncodedData(String encodedData) {
        this.encodedData = encodedData;
    }

    public JSONObject getRawData() {
        return rawData;
    }

    private void setRawData(JSONObject rawData) {
        this.rawData = rawData;
    }

    public JSONObject getSignatures() {
        return signatures;
    }

    private void setSignatures(JSONObject signatures) {
        this.signatures = signatures;
    }

    public String getStatus() {
        return status;
    }

    private void setStatus(String status) {
        this.status = status;
    }
}
