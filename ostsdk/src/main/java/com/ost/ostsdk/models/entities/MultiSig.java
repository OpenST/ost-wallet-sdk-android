package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "multi_sig")
public class MultiSig extends BaseEntity {

    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";
    public static final String TOKEN_HOLDER_ID = "token_holder_id";
    public static final String WALLETS = "wallets";
    public static final String REQUIREMENT = "requirement";
    public static final String AUTHORIZE_SESSION_CALL_PREFIX = "authorize_session_callprefix";

    @Ignore
    private String userId;
    @Ignore
    private String address;
    @Ignore
    private String tokenHolderId;
    @Ignore
    private String[] wallets;
    @Ignore
    private int requirement;
    @Ignore
    private String authorizeSessionCallPrefix;


    public MultiSig(JSONObject jsonObject) {
        super(jsonObject);
    }

    private MultiSig(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public MultiSig() {
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(MultiSig.USER_ID) &&
                jsonObject.has(MultiSig.ADDRESS) &&
                jsonObject.has(MultiSig.WALLETS) &&
                jsonObject.has(MultiSig.TOKEN_HOLDER_ID) &&
                jsonObject.has(MultiSig.REQUIREMENT) &&
                jsonObject.has(MultiSig.AUTHORIZE_SESSION_CALL_PREFIX);
    }

    @Override
    public void processJson(JSONObject jsonObject) {
        super.processJson(jsonObject);
    }

    public String getUserId() {
        return userId;
    }

    public String getAddress() {
        return address;
    }

    public String[] getWallets() {
        return wallets;
    }

    public int getRequirement() {
        return requirement;
    }

    public String getAuthorizeSessionCallPrefix() {
        return authorizeSessionCallPrefix;
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    private void setWallets(String[] wallets) {
        this.wallets = wallets;
    }

    private void setRequirement(int requirement) {
        this.requirement = requirement;
    }

    private void setAuthorizeSessionCallPrefix(String authorizeSessionCallPrefix) {
        this.authorizeSessionCallPrefix = authorizeSessionCallPrefix;
    }

    public String getTokenHolderId() {
        return tokenHolderId;
    }

    public void setTokenHolderId(String tokenHolderId) {
        this.tokenHolderId = tokenHolderId;
    }
}