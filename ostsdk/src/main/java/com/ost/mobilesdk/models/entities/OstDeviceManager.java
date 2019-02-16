package com.ost.mobilesdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.mobilesdk.models.Impls.OstModelFactory;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "device_manager")
public class OstDeviceManager extends OstBaseEntity {

    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";
    public static final String REQUIREMENT = "requirement";
    public static final String NONCE = "nonce";

    public static class CONST_STATUS {
        public static final String INITIALIZING = "initializing";
        public static final String ACTIVATED = "activated";
    }

    public static String getIdentifier() {
        return OstDeviceManager.ADDRESS;
    }

    public static OstDeviceManager parse(JSONObject jsonObject) throws JSONException {
        return (OstDeviceManager) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getDeviceManagerModel(), getIdentifier(), new EntityFactory() {
            @Override
            public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                return new OstDeviceManager(jsonObject);
            }
        });
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
        return getJSONData().optString(OstDeviceManager.USER_ID, null);
    }

    public String getAddress() {
        return getJSONData().optString(OstDeviceManager.ADDRESS, null);
    }

    public int getRequirement() {
        return getJSONData().optInt(OstDeviceManager.REQUIREMENT);
    }

    public OstDevice getDeviceMultiSigWallet() throws Exception {
        OstDevice deviceWallet = null;
        OstDevice wallets[] = OstModelFactory.getDeviceModel().getEntitiesByParentId(getId());
        for (OstDevice wallet : wallets) {
            if (null != new OstSecureKeyModelRepository().getByKey(wallet.getAddress())) {
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
        return getJSONData().optString(OstDeviceManager.NONCE, null);
    }

    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }

    @Override
    public String getParentIdKey() {
        return OstDeviceManager.USER_ID;
    }
}