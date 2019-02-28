package com.ost.mobilesdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.mobilesdk.models.Impls.OstModelFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Keys;


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
        public static final String CREATED = "created";
        public static final String RELAYING = "relaying";
        public static final String QUEUED = "queued";
        public static final String SUBMITTED = "submitted";
        public static final String SUCCESS = "success";
        public static final String FAIL = "fail";
    }

    public static class KIND_TYPE {
        public static final String AUTHORIZE_DEVICE = "authorize_device";
        public static final String REVOKE_DEVICE = "revode_device";
        public static final String SWAP_DEVICE = "swap_device";
        public static final String AUTHORIZE_RECOVERY_KEY = "authorize_recovery_key";
        public static final String REVOKE_RECOVERY_KEY = "revoke_recovery_key";
        public static final String SWAP_RECOVERY_KEY = "swap_recovery_key";
        public static final String AUTHORIZE_SESSION = "authorize_session";
        public static final String REVOKE_TOKEN_HOLDER_SESSION = "revoke_token_holder_session";
    }

    public static String getIdentifier() {
        return OstDeviceManagerOperation.ID;
    }

    private static EntityFactory entityFactory;
    private static EntityFactory getEntityFactory() {
        if ( null == entityFactory ) {
            entityFactory = new EntityFactory() {
                @Override
                public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                    return new OstDeviceManagerOperation(jsonObject);
                }
            };
        }
        return entityFactory;
    }

    public static OstDeviceManagerOperation parse(JSONObject jsonObject) throws JSONException {
        return (OstDeviceManagerOperation) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getDeviceManagerOperationModel(), getIdentifier(), getEntityFactory());
    }

    @Override
    protected OstDeviceManagerOperation updateWithJsonObject(JSONObject jsonObject) throws JSONException {
        return OstDeviceManagerOperation.parse(jsonObject);
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
        return this.getParentId();
    }

    public String getDeviceManagerId() {
        return this.getJsonDataPropertyAsString(OstDeviceManagerOperation.DEVICE_MANAGER_ID);
    }


    public String getKind() {
        return this.getJsonDataPropertyAsString(OstDeviceManagerOperation.KIND);
    }

    public String getDeviceManagerAddress() {
        String deviceManagerAddress = this.getJsonDataPropertyAsString(OstDeviceManagerOperation.DEVICE_MANAGER_ADDRESS);
        if (null != deviceManagerAddress) {
            deviceManagerAddress = Keys.toChecksumAddress(deviceManagerAddress);
        }
        return deviceManagerAddress;
    }

    public String getSafeTxnGas() {
        return this.getJsonDataPropertyAsString(OstDeviceManagerOperation.SAFE_TXN_GAS);
    }

    public JSONObject getSignatures() {
        //TO-DO: Make it safe.
        return getJSONData().optJSONObject(OstDeviceManagerOperation.SIGNATURES);
    }

    public String getCallData() {
        return this.getJsonDataPropertyAsString(OstDeviceManagerOperation.CALL_DATA);
    }

    public JSONObject getRawCallData() {
        //TO-DO: Make it safe.
        return getJSONData().optJSONObject(OstDeviceManagerOperation.RAW_CALL_DATA);
    }

    public String getOperation() {
        return this.getJsonDataPropertyAsString(OstDeviceManagerOperation.OPERATION);
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
