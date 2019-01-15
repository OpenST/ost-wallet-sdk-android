package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.models.Impls.ModelFactory;
import com.ost.ostsdk.models.Impls.SecureKeyModelRepository;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "multi_sig")
public class MultiSig extends BaseEntity {

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


    public MultiSig(JSONObject jsonObject) throws JSONException {
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
                jsonObject.has(MultiSig.TOKEN_HOLDER_ID) &&
                jsonObject.has(MultiSig.REQUIREMENT) &&
                jsonObject.has(MultiSig.AUTHORIZE_SESSION_CALL_PREFIX) &&
                jsonObject.has(MultiSig.NONCE);
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
        setAddress(jsonObject.getString(MultiSig.ADDRESS));
        setUserId(jsonObject.getString(MultiSig.USER_ID));

        setTokenHolderId(jsonObject.getString(MultiSig.TOKEN_HOLDER_ID));
        setRequirement(jsonObject.getInt(MultiSig.REQUIREMENT));
        setAuthorizeSessionCallPrefix(jsonObject.getString(MultiSig.AUTHORIZE_SESSION_CALL_PREFIX));
        setNonce(jsonObject.getString(MultiSig.NONCE));

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

    public MultiSigWallet getDeviceMultiSigWallet() throws Exception {
        MultiSigWallet deviceWallet = null;
        MultiSigWallet wallets[] = ModelFactory.getMultiSigWalletModel().getMultiSigWalletsByParentId(getId());
        for (MultiSigWallet wallet : wallets) {
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