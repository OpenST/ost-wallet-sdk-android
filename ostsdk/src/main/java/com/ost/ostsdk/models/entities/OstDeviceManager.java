package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.models.Impls.OstModelFactory;
import com.ost.ostsdk.models.Impls.OstSecureKeyModelRepository;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "device_manager")
public class OstDeviceManager extends OstBaseEntity {

    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";
    public static final String REQUIREMENT = "requirement";
    public static final String NONCE = "nonce";

    public static OstDeviceManager parse(JSONObject jsonObject) throws JSONException {
        OstDeviceManager ostDeviceManager = new OstDeviceManager(jsonObject);
        return OstModelFactory.getDeviceManagerModel().insert(ostDeviceManager);
    }

    public OstDeviceManager(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstDeviceManager(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstDeviceManager.USER_ID) &&
                jsonObject.has(OstDeviceManager.ADDRESS) &&
                jsonObject.has(OstDeviceManager.REQUIREMENT) &&
                jsonObject.has(OstDeviceManager.NONCE);
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
    }

    public String getUserId() {
        return getData().optString(OstDeviceManager.USER_ID, null);
    }

    public String getAddress() {
        return getData().optString(OstDeviceManager.ADDRESS, null);
    }

    public int getRequirement() {
        return getData().optInt(OstDeviceManager.REQUIREMENT);
    }

    public OstDevice getDeviceMultiSigWallet() throws Exception {
        OstDevice deviceWallet = null;
        OstDevice wallets[] = OstModelFactory.getDeviceModel().getDevicesByParentId(getId());
        for (OstDevice wallet : wallets) {
            if (null != new OstSecureKeyModelRepository().getById(wallet.getAddress())) {
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
        return getData().optString(OstDeviceManager.NONCE, null);
    }

    @Override
    String getEntityIdKey() {
        return OstDeviceManager.ID;
    }

    @Override
    public String getParentIdKey() {
        return OstDeviceManager.ADDRESS;
    }
}