package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.util.Log;

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
    public static final String TOKEN_HOLDER_ID = "token_holder_id";
    public static final String NAME = "name";
    public static final String MULTI_SIG_ID = "multi_sig_id";

    @Ignore
    private String tokenId;
    @Ignore
    private String tokenHolderId;
    @Ignore
    private String name;
    @Ignore
    private String multiSigId;

    public OstUser() {
    }

    public OstUser(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    private OstUser(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public String getTokenId() {
        return tokenId;
    }

    public String getTokenHolderId() {
        return tokenHolderId;
    }

    public String getName() {
        return name;
    }

    private void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public void setTokenHolderId(String tokenHolderId) {
        this.tokenHolderId = tokenHolderId;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String createDevice() {
        return new KeyGenProcess().execute(getId());
    }

    @Override
    public void processJson(JSONObject data) throws JSONException {
        super.processJson(data);
        setName(data.getString(OstUser.NAME));
        setTokenId(data.getString(OstUser.TOKEN_ID));
        setTokenHolderId(data.getString(OstUser.TOKEN_HOLDER_ID));
        setMultiSigId(data.getString(OstUser.MULTI_SIG_ID));
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstUser.TOKEN_ID) &&
                jsonObject.has(OstUser.TOKEN_HOLDER_ID) &&
                jsonObject.has(OstUser.NAME) &&
                jsonObject.has(OstUser.MULTI_SIG_ID);
    }

    public OstTokenHolder initTokenHolder(JSONObject jsonObject) throws JSONException {
        jsonObject.put(OstBaseEntity.PARENT_ID, getId());
        return OstModelFactory.getTokenHolderModel().initTokenHolder(jsonObject);
    }

    public OstTokenHolder getTokenHolder() {
        return OstModelFactory.getTokenHolderModel().getTokenHolderById(getTokenHolderId());
    }

    public void delTokenHolder(String id) {
        OstModelFactory.getTokenHolderModel().deleteTokenHolder(id);
    }

    public String getMultiSigId() {
        return multiSigId;
    }

    public void setMultiSigId(String multiSigId) {
        this.multiSigId = multiSigId;
    }

    public OstDeviceManager getMultiSig() {
        return OstModelFactory.getMultiSigModel().getMultiSigById(getMultiSigId());
    }

    @Override
    public void updateJSON() {
        super.updateJSON();
        try {
            JSONObject jsonObject = new JSONObject(getData());
            jsonObject.put(OstUser.MULTI_SIG_ID, getMultiSigId());
            jsonObject.put(OstUser.NAME, getName());
            jsonObject.put(OstUser.TOKEN_HOLDER_ID, getTokenHolderId());
            jsonObject.put(OstUser.TOKEN_ID, getTokenId());
            setData(jsonObject.toString());
        } catch (JSONException jsonException) {
            Log.e(TAG, "Unexpected exception while parsing JSON String");
            throw new RuntimeException("Unexpected exception while parsing JSON String");
        }
    }
}