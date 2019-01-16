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
    String getEntityIdKey() {
        return OstUser.ID;
    }

    @Override
    public String getParentIdKey() {
        return OstUser.TOKEN_ID;
    }
}