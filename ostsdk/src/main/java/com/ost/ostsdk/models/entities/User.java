package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ost.ostsdk.models.Impls.ModelFactory;
import com.ost.ostsdk.models.TaskCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Manage transaction signing
 */
@Entity(tableName = "user")
public class User extends BaseEntity {

    private static final String TAG = "User";

    public static final String ECONOMY_ID = "economy_id";
    public static final String TOKEN_HOLDER_ID = "token_holder_id";
    public static final String NAME = "name";
    public static final String MULTI_SIG_ID = "multi_sig_id";

    @Ignore
    private String economyId;
    @Ignore
    private String tokenHolderId;
    @Ignore
    private String name;
    @Ignore
    private String multiSigId;

    public User() {
    }

    public User(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    private User(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public String getEconomyId() {
        return economyId;
    }

    public String getTokenHolderId() {
        return tokenHolderId;
    }

    public String getName() {
        return name;
    }

    private void setEconomyId(String economyId) {
        this.economyId = economyId;
    }

    public void setTokenHolderId(String tokenHolderId) {
        this.tokenHolderId = tokenHolderId;
    }

    private void setName(String name) {
        this.name = name;
    }


    @Override
    public void processJson(JSONObject data) throws JSONException {
        super.processJson(data);
        setName(data.getString(User.NAME));
        setEconomyId(data.getString(User.ECONOMY_ID));
        setTokenHolderId(data.getString(User.TOKEN_HOLDER_ID));
        setMultiSigId(data.getString(User.MULTI_SIG_ID));
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(User.ECONOMY_ID) &&
                jsonObject.has(User.TOKEN_HOLDER_ID) &&
                jsonObject.has(User.NAME) &&
                jsonObject.has(User.MULTI_SIG_ID);
    }

    public TokenHolder initTokenHolder(JSONObject jsonObject, @NonNull TaskCallback callback) throws JSONException {
        jsonObject.put(BaseEntity.PARENT_ID, getId());
        return ModelFactory.getTokenHolderModel().initTokenHolder(jsonObject, callback);
    }

    public TokenHolder initTokenHolder(JSONObject jsonObject) throws JSONException {
        return initTokenHolder(jsonObject, new TaskCallback() {
        });
    }

    public TokenHolder getTokenHolder() {
        return ModelFactory.getTokenHolderModel().getTokenHolderById(getTokenHolderId());
    }

    public void delTokenHolder(String id, @NonNull TaskCallback callback) {
        ModelFactory.getTokenHolderModel().deleteTokenHolder(id, callback);
    }

    public void delTokenHolder(String id) {
        delTokenHolder(id, new TaskCallback() {
        });
    }

    public String getMultiSigId() {
        return multiSigId;
    }

    public void setMultiSigId(String multiSigId) {
        this.multiSigId = multiSigId;
    }

    public MultiSig getMultiSig() {
        return ModelFactory.getMultiSig().getMultiSigById(getMultiSigId());
    }

    @Override
    public void updateJSON() {
        super.updateJSON();
        try {
            JSONObject jsonObject = new JSONObject(getData());
            jsonObject.put(User.MULTI_SIG_ID, getMultiSigId());
            jsonObject.put(User.NAME, getName());
            jsonObject.put(User.TOKEN_HOLDER_ID, getTokenHolderId());
            jsonObject.put(User.ECONOMY_ID, getEconomyId());
            setData(jsonObject.toString());
        } catch (JSONException jsonException) {
            Log.e(TAG, "Unexpected exception while parsing JSON String");
            throw new RuntimeException("Unexpected exception while parsing JSON String");
        }
    }
}