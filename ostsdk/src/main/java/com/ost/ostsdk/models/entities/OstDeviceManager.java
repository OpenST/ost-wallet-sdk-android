package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.models.Impls.ModelFactory;
import com.ost.ostsdk.models.Impls.SecureKeyModelRepository;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "device_manager")
public class OstDeviceManager extends OstBaseEntity {

    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";
    public static final String TOKEN_HOLDER_ID = "token_holder_id";
    public static final String REQUIREMENT = "requirement";
    public static final String AUTHORIZE_SESSION_CALL_PREFIX = "authorize_session_callprefix";
    public static final String NONCE = "nonce";

    @Ignore
    private String userId;
    @Ignore
    private String address;
    @Ignore
    private String tokenHolderId;
    @Ignore
    private int requirement;
    @Ignore
    private String authorizeSessionCallPrefix;
    @Ignore
    private String nonce;


    public OstDeviceManager(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    private OstDeviceManager(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public OstDeviceManager() {
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstDeviceManager.USER_ID) &&
                jsonObject.has(OstDeviceManager.ADDRESS) &&
                jsonObject.has(OstDeviceManager.TOKEN_HOLDER_ID) &&
                jsonObject.has(OstDeviceManager.REQUIREMENT) &&
                jsonObject.has(OstDeviceManager.AUTHORIZE_SESSION_CALL_PREFIX) &&
                jsonObject.has(OstDeviceManager.NONCE);
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
        setAddress(jsonObject.getString(OstDeviceManager.ADDRESS));
        setUserId(jsonObject.getString(OstDeviceManager.USER_ID));

        setTokenHolderId(jsonObject.getString(OstDeviceManager.TOKEN_HOLDER_ID));
        setRequirement(jsonObject.getInt(OstDeviceManager.REQUIREMENT));
        setAuthorizeSessionCallPrefix(jsonObject.getString(OstDeviceManager.AUTHORIZE_SESSION_CALL_PREFIX));
        setNonce(jsonObject.getString(OstDeviceManager.NONCE));

    }

    public String getUserId() {
        return userId;
    }

    public String getAddress() {
        return address;
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

    public OstDevice getDeviceMultiSigWallet() throws Exception {
        OstDevice deviceWallet = null;
        OstDevice wallets[] = ModelFactory.getMultiSigWalletModel().getMultiSigWalletsByParentId(getId());
        for (OstDevice wallet : wallets) {
            if (null != new SecureKeyModelRepository().getById(wallet.getAddress())) {
                deviceWallet = wallet;
                break;
            }
        }
        if (null == deviceWallet) {
            throw new Exception("Wallet not found in db");
        }
        return deviceWallet;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}