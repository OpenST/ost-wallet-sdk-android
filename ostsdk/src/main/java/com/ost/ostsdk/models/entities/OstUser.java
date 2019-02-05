package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.models.Impls.OstModelFactory;
import com.ost.ostsdk.security.OstKeyManager;
import com.ost.ostsdk.utils.KeyGenProcess;

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
    public static final String NAME = "name";
    public static final String DEVICE_MANAGER_ADDRESS = "device_manager_address";
    public static final String TYPE = "type";

    public static String getIdentifier() {
        return OstUser.ID;
    }

    @Ignore
    private OstDevice currentDevice = null;

    public OstDevice getCurrentDevice() {
        if (null == currentDevice) {
            String currentDeiceAddress = null;
            OstDevice[] ostDevices = OstDevice.getDevicesByParentId(getId());
            OstKeyManager ostKeyManager = new OstKeyManager(getId());
            for (OstDevice device : ostDevices) {
                if (ostKeyManager.hasAddress(device.getAddress())) {
                    currentDeiceAddress = device.getAddress();
                }
            }
            if (null == currentDeiceAddress) {
                throw new RuntimeException("Unexpected Error");
            }
            currentDevice = OstDevice.getById(currentDeiceAddress);
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

    public String getName() {
        return getJSONData().optString(OstUser.NAME,null);
    }

    public String getDeviceManagerAddress() {
        return getJSONData().optString(OstUser.DEVICE_MANAGER_ADDRESS,null);
    }

    public String getType() {
        return getJSONData().optString(OstUser.TYPE,null);
    }

    public String createDevice() {
        return new KeyGenProcess().execute(getId());
    }

    @Override
    public void processJson(JSONObject data) throws JSONException {
        super.processJson(data);
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstUser.TOKEN_ID) &&
                jsonObject.has(OstUser.TOKEN_HOLDER_ADDRESS) &&
                jsonObject.has(OstUser.NAME) &&
                jsonObject.has(OstUser.TYPE) &&
                jsonObject.has(OstUser.DEVICE_MANAGER_ADDRESS);
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