package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "device_operation")
public class OstDeviceOperation extends OstBaseEntity {

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


    public OstDeviceOperation(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    private OstDeviceOperation(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public OstDeviceOperation() {
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstDeviceOperation.USER_ID) &&
                jsonObject.has(OstDeviceOperation.STATUS) &&
                jsonObject.has(OstDeviceOperation.TOKEN_HOLDER_ADDRESS) &&
                jsonObject.has(OstDeviceOperation.KIND) &&
                jsonObject.has(OstDeviceOperation.ENCODED_DATA) &&
                jsonObject.has(OstDeviceOperation.SIGNATURES) &&
                jsonObject.has(OstDeviceOperation.RAW_DATA);


    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
        setUserId(jsonObject.getString(OstDeviceOperation.USER_ID));
        setStatus(jsonObject.getString(OstDeviceOperation.STATUS));
        setTokenHolderAddress(jsonObject.getString(OstDeviceOperation.TOKEN_HOLDER_ADDRESS));
        setKind(jsonObject.getString(OstDeviceOperation.KIND));
        setEncodedData(jsonObject.getString(OstDeviceOperation.ENCODED_DATA));
        setSignatures(jsonObject.getJSONObject(OstDeviceOperation.SIGNATURES));
        setRawData(jsonObject.getJSONObject(OstDeviceOperation.RAW_DATA));
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
