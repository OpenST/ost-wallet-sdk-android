package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import com.ost.ostsdk.models.Impls.ModelFactory;
import com.ost.ostsdk.models.TaskCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Manage transaction signing
 */
@Entity(tableName = "user")
public class User extends BaseEntity {

    public static final String ECONOMY_ID = "economy_id";
    public static final String TOKEN_HOLDER_ID = "token_holder_id";
    public static final String NAME = "name";

    @Ignore
    private String economyId;
    @Ignore
    private String tokenHolderId;
    @Ignore
    private String name;

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

    private void setTokenHolderId(String tokenHolderId) {
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
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(User.ECONOMY_ID) &&
                jsonObject.has(User.TOKEN_HOLDER_ID) &&
                jsonObject.has(User.NAME);
    }

    public TokenHolder initTokenHolder(JSONObject jsonObject, @NonNull TaskCallback callback) throws JSONException {
        jsonObject.put(BaseEntity.PARENT_ID, getId());
        return ModelFactory.getTokenHolderModel().initTokenHolder(jsonObject, callback);
    }

    public TokenHolder initTokenHolder(JSONObject jsonObject) throws JSONException {
        return initTokenHolder(jsonObject, new TaskCallback() {
        });
    }

    public TokenHolder getTokenHolder(String id) {
        return ModelFactory.getTokenHolderModel().getTokenHolderById(id);
    }

    public void delTokenHolder(String id, @NonNull TaskCallback callback) {
        ModelFactory.getTokenHolderModel().deleteTokenHolder(id, callback);
    }

    public void delTokenHolder(String id) {
        delTokenHolder(id, new TaskCallback() {
        });
    }
}