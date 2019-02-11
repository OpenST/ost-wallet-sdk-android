package com.ost.mobilesdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.util.Log;

import com.ost.mobilesdk.models.Impls.OstModelFactory;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.KeyGenProcess;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Manage transaction signing
 */
@Entity(tableName = "user")
public class OstUser extends OstBaseEntity {

    private static final String TAG = "OstUser";

    public static final String TOKEN_ID = "token_id";
    public static final String TOKEN_HOLDER_ADDRESS = "token_holder_address";
    public static final String DEVICE_MANAGER_ADDRESS = "device_manager_address";
    public static final String TYPE = "type";

    public static String getIdentifier() {
        return OstUser.ID;
    }

    @Ignore
    private OstDevice currentDevice = null;

    public OstDevice getCurrentDevice() {
        if (null == currentDevice) {
            Log.d(TAG, "currentDevice is null");
            OstKeyManager ostKeyManager = new OstKeyManager(getId());
            String currentDeviceAddress = ostKeyManager.getDeviceAddress();
            if (null != currentDeviceAddress) {
                currentDevice = OstDevice.getById(currentDeviceAddress);
                Log.d(TAG, String.format("currentDeviceAddress: %s", currentDeviceAddress));
            }
        }
        return currentDevice;
    }

    public static class CONST_STATUS {
        public static final String CREATED = "CREATED";
        public static final String ACTIVATING = "ACTIVATING";
        public static final String ACTIVATED = "ACTIVATED";
    }

    public static class TYPE_VALUE {
        public static final String USER = "admin";
        public static final String ADMIN = "user";
    }

    public static OstUser parse(JSONObject jsonObject) throws JSONException {
        return (OstUser) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getUserModel(), getIdentifier(), new EntityFactory() {
            @Override
            public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                return new OstUser(jsonObject);
            }
        });
    }

    public OstUser(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstUser(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public String getTokenId() {
        return getJSONData().optString(OstUser.TOKEN_ID,null);
    }

    public String getTokenHolderAddress() {
        return getJSONData().optString(OstUser.TOKEN_HOLDER_ADDRESS,null);
    }


    public String getDeviceManagerAddress() {
        return getJSONData().optString(OstUser.DEVICE_MANAGER_ADDRESS,null);
    }

    public String getType() {
        return getJSONData().optString(OstUser.TYPE,null);
    }

    public OstDevice createDevice() {
        OstKeyManager ostKeyManager = new OstKeyManager(getId());
        String apiAddress = ostKeyManager.getApiKeyAddress();
        String address = ostKeyManager.createKey();
        OstDevice ostDevice = OstDevice.init(address, apiAddress, getId());
        return ostDevice;
    }

    @Override
    public void processJson(JSONObject data) throws JSONException {
        super.processJson(data);
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstUser.ID) &&
                jsonObject.has(OstUser.TOKEN_ID) &&
                jsonObject.has(OstUser.TYPE);
    }

    public OstTokenHolder getTokenHolder() {
        return OstModelFactory.getTokenHolderModel().getEntityById(getTokenHolderAddress());
    }

    public void delTokenHolder(String id) {
        OstModelFactory.getTokenHolderModel().deleteEntity(id);
    }

    public OstDeviceManager getMultiSig() {
        return OstModelFactory.getDeviceManagerModel().getEntityById(getDeviceManagerAddress());
    }

    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }

    @Override
    public String getParentIdKey() {
        return OstUser.TOKEN_ID;
    }
}