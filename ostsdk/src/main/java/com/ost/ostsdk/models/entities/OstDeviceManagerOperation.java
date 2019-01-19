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

    public static class CONST_STATUS {
        public static final String CREATED = "CREATED";
        public static final String RELAYING = "RELAYING";
        public static final String QUEUED = "QUEUED";
        public static final String SUBMITTED = "SUBMITTED";
        public static final String SUCCESS = "SUCCESS";
        public static final String FAIL = "FAIL";
    }

    public static class KIND {
        public static final String AUTHORIZE_DEVICE = "AUTHORIZE_DEVICE";
        public static final String REVOKE_DEVICE = "REVOKE_DEVICE";
        public static final String SWAP_DEVICE = "SWAP_DEVICE";
        public static final String AUTHORIZE_RECOVERY_KEY = "AUTHORIZE_RECOVERY_KEY";
        public static final String REVOKE_RECOVERY_KEY = "REVOKE_RECOVERY_KEY";
        public static final String SWAP_RECOVERY_KEY = "SWAP_RECOVERY_KEY";
        public static final String AUTHORIZE_TOKEN_HOLDER_SESSION = "AUTHORIZE_TOKEN_HOLDER_SESSION";
        public static final String REVOKE_TOKEN_HOLDER_SESSION = "REVOKE_TOKEN_HOLDER_SESSION";
    }

    public static String getIdentifier() {
        return OstUser.ID;
    }

    public static OstDeviceManagerOperation parse(JSONObject jsonObject) throws JSONException {
        return (OstDeviceManagerOperation) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getDeviceManagerOperationModel(), getIdentifier(), new EntityFactory() {
            @Override
            public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                return new OstDeviceManagerOperation(jsonObject);
            }
        });
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
        return getJSONData().optString(OstDeviceManagerOperation.USER_ID, null);
    }

    public String getDeviceManagerId() {
        return getJSONData().optString(OstDeviceManagerOperation.DEVICE_MANAGER_ID, null);
    }


    public String getKind() {
        return getJSONData().optString(OstDeviceManagerOperation.KIND, null);
    }

    public String getDeviceManagerAddress() {
        return getJSONData().optString(OstDeviceManagerOperation.DEVICE_MANAGER_ADDRESS, null);
    }

    public String getSafeTxnGas() {
        return getJSONData().optString(OstDeviceManagerOperation.SAFE_TXN_GAS, null);
    }

    public JSONObject getSignatures() {
        return getJSONData().optJSONObject(OstDeviceManagerOperation.SIGNATURES);
    }

    public String getCallData() {
        return getJSONData().optString(OstDeviceManagerOperation.CALL_DATA, null);
    }

    public JSONObject getRawCallData() {
        return getJSONData().optJSONObject(OstDeviceManagerOperation.RAW_CALL_DATA);
    }

    public String getOperation() {
        return getJSONData().optString(OstDeviceManagerOperation.OPERATION, null);
    }

    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }

    @Override
    public String getParentIdKey() {
        return OstDeviceManagerOperation.USER_ID;
    }
}
