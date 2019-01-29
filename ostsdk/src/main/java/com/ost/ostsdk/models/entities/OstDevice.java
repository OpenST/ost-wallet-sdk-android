package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.OstModelFactory;
import com.ost.ostsdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.ostsdk.security.impls.OstAndroidSecureStorage;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

/**
 * Transaction Signing
 */
@Entity(tableName = "device")
public class OstDevice extends OstBaseEntity {

    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";
    public static final String DEVICE_MANAGER_ADDRESS = "device_manager_address";
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_UUID = "device_uuid";
    public static final String DEVICE_MODEL = "device_model";

    public static class CONST_STATUS {
        public static final String CREATED = "CREATED";
        public static final String REGISTERED = "REGISTERED";
        public static final String AUTHORIZING = "AUTHORIZING";
        public static final String AUTHORIZED = "AUTHORIZED";
        public static final String REVOKING = "REVOKING";
        public static final String REVOKED = "REVOKED";
    }

    public static String getIdentifier() {
        return OstDevice.ADDRESS;
    }

    public static OstDevice parse(JSONObject jsonObject) throws JSONException {
        return (OstDevice) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getDeviceModel(), getIdentifier(), new EntityFactory() {
            @Override
            public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                return new OstDevice(jsonObject);
            }
        });
    }

    public OstDevice(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstDevice(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstDevice.ADDRESS) &&
                jsonObject.has(OstDevice.STATUS) &&
                jsonObject.has(OstDevice.DEVICE_NAME) &&
                jsonObject.has(OstDevice.DEVICE_MODEL) &&
                jsonObject.has(OstDevice.DEVICE_UUID) &&
                jsonObject.has(OstDevice.DEVICE_MANAGER_ADDRESS);
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
    }

    public String signTransaction(RawTransaction rawTransaction, String userId) {
        byte[] data = new OstSecureKeyModelRepository().getByKey(getAddress()).getData();
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(Numeric.toHexString(OstAndroidSecureStorage.getInstance(OstSdk.getContext(), userId).decrypt(data))));
        return Numeric.toHexString(signedMessage);
    }


    public String getAddress() {
        return getJSONData().optString(OstDevice.ADDRESS, null);
    }

    public String getDeviceName() {
        return getJSONData().optString(OstDevice.DEVICE_NAME, null);
    }

    public String getDeviceModel() {
        return getJSONData().optString(OstDevice.DEVICE_MODEL, null);
    }

    public String getDeviceUuid() {
        return getJSONData().optString(OstDevice.DEVICE_UUID, null);
    }

    public String getUserId() {
        return getJSONData().optString(OstDevice.USER_ID, null);
    }

    public String getDeviceManagerAddress() {
        return getJSONData().optString(OstDevice.DEVICE_MANAGER_ADDRESS, null);
    }

    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }

    @Override
    public String getParentIdKey() {
        return OstDevice.USER_ID;
    }

    public static class Transaction extends RawTransaction {

        public Transaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data) {
            super(nonce, gasPrice, gasLimit, to, value, data);
        }
    }
}