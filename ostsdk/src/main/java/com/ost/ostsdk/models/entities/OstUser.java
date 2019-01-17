package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.models.Impls.OstModelFactory;
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


    public static OstUser parse(JSONObject jsonObject) throws JSONException {
        OstUser ostUser = new OstUser(jsonObject);
        return OstModelFactory.getUserModel().insert(ostUser);
    }

    public OstUser(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    private OstUser(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public String getTokenId() {
        return getData().optString(OstUser.TOKEN_ID,null);
    }

    public String getTokenHolderAddress() {
        return getData().optString(OstUser.TOKEN_HOLDER_ADDRESS,null);
    }

    public String getName() {
        return getData().optString(OstUser.NAME,null);
    }

    public String getDeviceManagerAddress() {
        return getData().optString(OstUser.DEVICE_MANAGER_ADDRESS,null);
    }

    public String getType() {
        return getData().optString(OstUser.TYPE,null);
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
        return OstModelFactory.getTokenHolderModel().getTokenHolderById(getTokenHolderAddress());
    }

    public void delTokenHolder(String id) {
        OstModelFactory.getTokenHolderModel().deleteTokenHolder(id);
    }

    public OstDeviceManager getMultiSig() {
        return OstModelFactory.getDeviceManagerModel().getMultiSigById(getDeviceManagerAddress());
    }

    @Override
    String getEntityIdKey() {
        return OstUser.ID;
    }

    @Override
    public String getParentIdKey() {
        return OstUser.TOKEN_ID;
    }
}