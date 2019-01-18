package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.models.Impls.OstModelFactory;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "device_operation")
public class OstDeviceManagerOperation extends OstBaseEntity {

    public static final String USER_ID = "user_id";
    public static final String DEVICE_MANAGER_ID = "device_manager_id";
    public static final String DEVICE_MANAGER_ADDRESS = "device_manager_address";
    public static final String KIND = "kind";
    public static final String OPERATION = "operation";
    public static final String SAFE_TXN_GAS = "safe_txn_gas";
    public static final String CALL_DATA = "call_data";
    public static final String RAW_CALL_DATA = "raw_call_data";
    public static final String SIGNATURES = "signatures";

    public static OstDeviceManagerOperation parse(JSONObject jsonObject) throws JSONException {
        OstDeviceManagerOperation ostDeviceManagerOperation = new OstDeviceManagerOperation(jsonObject);
        return OstModelFactory.getDeviceManagerOperationModel().insert(ostDeviceManagerOperation);
    }

    public OstDeviceManagerOperation(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstDeviceManagerOperation(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstDeviceManagerOperation.USER_ID) &&
                jsonObject.has(OstDeviceManagerOperation.DEVICE_MANAGER_ID) &&
                jsonObject.has(OstDeviceManagerOperation.KIND) &&
                jsonObject.has(OstDeviceManagerOperation.DEVICE_MANAGER_ADDRESS) &&
                jsonObject.has(OstDeviceManagerOperation.SAFE_TXN_GAS) &&
                jsonObject.has(OstDeviceManagerOperation.SIGNATURES) &&
                jsonObject.has(OstDeviceManagerOperation.CALL_DATA) &&
                jsonObject.has(OstDeviceManagerOperation.RAW_CALL_DATA) &&
                jsonObject.has(OstDeviceManagerOperation.OPERATION);


    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
    }

    public String getUserId() {
        return getData().optString(OstDeviceManagerOperation.USER_ID, null);
    }

    public String getDeviceManagerId() {
        return getData().optString(OstDeviceManagerOperation.DEVICE_MANAGER_ID, null);
    }


    public String getKind() {
        return getData().optString(OstDeviceManagerOperation.KIND, null);
    }

    public String getDeviceManagerAddress() {
        return getData().optString(OstDeviceManagerOperation.DEVICE_MANAGER_ADDRESS, null);
    }

    public String getSafeTxnGas() {
        return getData().optString(OstDeviceManagerOperation.SAFE_TXN_GAS, null);
    }

    public JSONObject getSignatures() {
        return getData().optJSONObject(OstDeviceManagerOperation.SIGNATURES);
    }

    public String getCallData() {
        return getData().optString(OstDeviceManagerOperation.CALL_DATA, null);
    }

    public JSONObject getRawCallData() {
        return getData().optJSONObject(OstDeviceManagerOperation.RAW_CALL_DATA);
    }

    public String getOperation() {
        return getData().optString(OstDeviceManagerOperation.OPERATION, null);
    }

    @Override
    String getEntityIdKey() {
        return OstUser.ID;
    }

    @Override
    public String getParentIdKey() {
        return OstDeviceManagerOperation.USER_ID;
    }
}
