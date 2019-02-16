package com.ost.mobilesdk.models.entities;


import android.Manifest;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstModelFactory;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.mobilesdk.models.OstDeviceModel;
import com.ost.mobilesdk.security.impls.OstAndroidSecureStorage;

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
    public static final String PERSONAL_SIGN_ADDRESS = "api_signer_address";

    public static OstDevice getById(String id) {
        OstDeviceModel ostDeviceModel = OstModelFactory.getDeviceModel();
        return ostDeviceModel.getEntityById(id);
    }


    public static class CONST_STATUS {
        public static final String CREATED = "created";
        public static final String REGISTERED = "registered";
        public static final String AUTHORIZING = "authorizing";
        public static final String AUTHORIZED = "authorized";
        public static final String REVOKING = "revoking";
        public static final String REVOKED = "revoked";
    }

    public static OstDevice init(String address, String apiAddress, String mUserId) {
        TelephonyManager tManager = (TelephonyManager) OstSdk.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String uuid = "uuid";
        if (ActivityCompat.checkSelfPermission(OstSdk.getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            uuid = tManager.getDeviceId();
        }

        String deviceName = android.os.Build.MANUFACTURER + android.os.Build.PRODUCT;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OstDevice.ADDRESS, address);
            jsonObject.put(OstDevice.PERSONAL_SIGN_ADDRESS, apiAddress);
            jsonObject.put(OstDevice.USER_ID, mUserId);
            jsonObject.put(OstDevice.DEVICE_NAME, deviceName);
            jsonObject.put(OstDevice.DEVICE_UUID, uuid);
            jsonObject.put(OstDevice.DEVICE_MANAGER_ADDRESS, "");
            jsonObject.put(OstDevice.UPDATED_TIMESTAMP, System.currentTimeMillis());
            jsonObject.put(OstDevice.STATUS, CONST_STATUS.CREATED);
            return OstDevice.parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static OstDevice[] getDevicesByParentId(String mUserId) {
        OstDeviceModel ostDeviceModel = OstModelFactory.getDeviceModel();
        return ostDeviceModel.getEntitiesByParentId(mUserId);
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
                jsonObject.has(OstDevice.PERSONAL_SIGN_ADDRESS);
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

    public String getPersonalSignAddress() {
        return getJSONData().optString(OstDevice.PERSONAL_SIGN_ADDRESS, null);
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
    public String getStatus() {
        return getJSONData().optString(OstDevice.STATUS, null);
    }

    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }

    @Override
    public String getParentIdKey() {
        return OstDevice.USER_ID;
    }

    @Override
    public String getDefaultStatus() {
        return CONST_STATUS.CREATED;
    }

    public static class Transaction extends RawTransaction {

        public Transaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data) {
            super(nonce, gasPrice, gasLimit, to, value, data);
        }
    }
}