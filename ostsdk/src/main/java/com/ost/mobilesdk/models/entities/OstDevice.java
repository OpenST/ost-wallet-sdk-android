package com.ost.mobilesdk.models.entities;


import android.Manifest;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

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

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Transaction Signing
 */
@Entity(tableName = "device")
public class OstDevice extends OstBaseEntity {

    public static final String TAG = "OstDeviceEntity";
    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";
    public static final String DEVICE_MANAGER_ADDRESS = "device_manager_address";
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_UUID = "device_uuid";
    public static final String API_SIGNER_ADDRESS = "api_signer_address";

    public static OstDevice getById(String id) {
        OstDeviceModel ostDeviceModel = OstModelFactory.getDeviceModel();
        return ostDeviceModel.getEntityById(id);
    }

    public static boolean isValidStatus(String status) {
        return Arrays.asList(OstDevice.CONST_STATUS.CREATED, OstDevice.CONST_STATUS.REGISTERED,
                CONST_STATUS.AUTHORIZING, CONST_STATUS.AUTHORIZED, CONST_STATUS.REVOKING,
                CONST_STATUS.REVOKED).contains(status);
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
            jsonObject.put(OstDevice.API_SIGNER_ADDRESS, apiAddress);
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

    public static OstDevice[] getDevicesByParentId(String userId) {
        if (TextUtils.isEmpty(userId) ) {
            return (OstDevice[]) Arrays.asList().toArray();
        }

        OstDeviceModel ostDeviceModel = OstModelFactory.getDeviceModel();
        return ostDeviceModel.getEntitiesByParentId(userId);
    }

    public static String getIdentifier() {
        return OstDevice.ADDRESS;
    }

    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }

    private static EntityFactory entityFactory;
    private static EntityFactory getEntityFactory() {
        if ( null == entityFactory ) {
            entityFactory = new EntityFactory() {
                @Override
                public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                    return new OstDevice(jsonObject);
                }
            };
        }
        return entityFactory;
    }

    public static OstDevice parse(JSONObject jsonObject) throws JSONException {
        return (OstDevice) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getDeviceModel(), getIdentifier(), getEntityFactory());
    }

    @Override
    protected OstDevice updateWithJsonObject(JSONObject jsonObject) throws JSONException {
        return OstDevice.parse(jsonObject);
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
                jsonObject.has(OstDevice.API_SIGNER_ADDRESS);
    }

    public String getAddress() {
        return this.getId();
    }

    public String getDeviceName() {
        JSONObject jsonObject = this.getJSONData();
        if ( null == jsonObject ) {
            Log.e(TAG, "getDeviceName: jsonObject is null");
            return null;
        }
        return jsonObject.optString(OstDevice.DEVICE_NAME,null);
    }

    public String getPersonalSignAddress() {
        JSONObject jsonObject = this.getJSONData();
        if ( null == jsonObject ) {
            Log.e(TAG, "getPersonalSignAddress: jsonObject is null");
            return null;
        }
        return jsonObject.optString(OstDevice.API_SIGNER_ADDRESS,null);
    }

    public String getDeviceUuid() {
        JSONObject jsonObject = this.getJSONData();
        if ( null == jsonObject ) {
            Log.e(TAG, "getDeviceUuid: jsonObject is null");
            return null;
        }
        return jsonObject.optString(OstDevice.DEVICE_UUID,null);
    }

    public String getUserId() {
        return this.getParentId();
    }

    public String getDeviceManagerAddress() {
        JSONObject jsonObject = this.getJSONData();
        if ( null == jsonObject ) {
            Log.e(TAG, "getDeviceManagerAddress: jsonObject is null");
            return null;
        }
        return jsonObject.optString(OstDevice.DEVICE_MANAGER_ADDRESS, null);
    }

    @Override
    public String getParentIdKey() {
        return OstDevice.USER_ID;
    }

    @Override
    public String getDefaultStatus() {
        return CONST_STATUS.CREATED;
    }

    public boolean canMakeApiCall() {
        String status = this.getStatus();
        return OstDevice.CONST_STATUS.AUTHORIZED.equalsIgnoreCase(status)
                || OstDevice.CONST_STATUS.REGISTERED.equalsIgnoreCase(status)
                || OstDevice.CONST_STATUS.AUTHORIZING.equalsIgnoreCase(status);
    }
}