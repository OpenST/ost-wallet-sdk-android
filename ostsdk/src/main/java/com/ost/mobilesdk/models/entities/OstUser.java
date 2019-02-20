package com.ost.mobilesdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.util.Log;

import com.ost.mobilesdk.models.Impls.OstModelFactory;
import com.ost.mobilesdk.security.OstKeyManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.utils.Numeric;

import java.util.Arrays;
import java.util.List;

/**
 * To store user entity
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

    public static OstUser getById(String id) {
        return OstModelFactory.getUserModel().getEntityById(id);
    }

    public static OstUser initUser(String id, String tokenId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(OstUser.ID, id);
            jsonObject.put(OstUser.TOKEN_ID, tokenId);
//            jsonObject.put(OstUser.TOKEN_HOLDER_ADDRESS, "");
//            jsonObject.put(OstUser.DEVICE_MANAGER_ADDRESS, "");
//            jsonObject.put(OstUser.TYPE, "");
            return OstUser.parse(jsonObject);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected error: OstUser json parse exception");
        }
        return null;
    }

    public OstDevice getCurrentDevice() {
        if (null == currentDevice) {
            OstKeyManager ostKeyManager = new OstKeyManager(getId());
            String currentDeviceAddress = ostKeyManager.getDeviceAddress();
            if (null != currentDeviceAddress) {
                currentDevice = OstDevice.getById(currentDeviceAddress);
                Log.d(TAG, String.format("currentDeviceAddress: %s", currentDeviceAddress));
            }
        }
        return currentDevice;
    }

    public static OstSession getActiveSession(String userId) {
        List<OstSession> ostActiveSessionList = OstSession.getActiveSessions(userId);
        // Todo: Logic to filter most appropriate session.
        return ostActiveSessionList.get(0);
    }

    public String sign(String messageToSign) {
        OstKeyManager ostKeyManager = new OstKeyManager(getId());
        return ostKeyManager.sign(getCurrentDevice().getAddress(), Numeric.hexStringToByteArray(messageToSign));
    }

    public static class CONST_STATUS {
        public static final String CREATED = "created";
        public static final String ACTIVATING = "activating";
        public static final String ACTIVATED = "activated";
    }

    public static boolean isValidStatus(String status) {
        return Arrays.asList(CONST_STATUS.CREATED, CONST_STATUS.ACTIVATING, CONST_STATUS.ACTIVATED).contains(status);
    }

    public static class TYPE_VALUE {
        public static final String USER = "admin";
        public static final String ADMIN = "user";
    }

    public static OstUser parse(JSONObject jsonObject) throws JSONException {
        return (OstUser) OstBaseEntity.insertOrUpdate(jsonObject, OstModelFactory.getUserModel(), getIdentifier(), new EntityFactory() {
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
        return getJSONData().optString(OstUser.TOKEN_ID, null);
    }

    public String getTokenHolderAddress() {
        return getJSONData().optString(OstUser.TOKEN_HOLDER_ADDRESS, null);
    }


    public String getDeviceManagerAddress() {
        return getJSONData().optString(OstUser.DEVICE_MANAGER_ADDRESS, null);
    }

    public String getType() {
        return getJSONData().optString(OstUser.TYPE, null);
    }

    public OstDevice createDevice() {
        OstKeyManager ostKeyManager = new OstKeyManager(getId());
        String apiAddress = ostKeyManager.getApiKeyAddress();
        String address = ostKeyManager.getDeviceAddress();
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
                jsonObject.has(OstUser.TOKEN_ID);
    }

    public OstTokenHolder getTokenHolder() {
        String tokenHolderAddress = getTokenHolderAddress();
        if ( null == tokenHolderAddress ) {
            return null;
        }
        return OstModelFactory.getTokenHolderModel().getEntityById(tokenHolderAddress);
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