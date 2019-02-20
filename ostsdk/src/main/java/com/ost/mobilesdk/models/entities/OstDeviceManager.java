package com.ost.mobilesdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.util.Log;

import com.ost.mobilesdk.models.Impls.OstModelFactory;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "device_manager")
public class OstDeviceManager extends OstBaseEntity {
    private static final String TAG = "OstDeviceManagerEntity";
    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";
    public static final String REQUIREMENT = "requirement";
    public static final String NONCE = "nonce";

    public static OstDeviceManager getById(String deviceManagerAddress) {
        return OstModelFactory.getDeviceManagerModel().getEntityById(deviceManagerAddress);
    }

    public static class CONST_STATUS {
        public static final String ACTIVATED = "activated";
    }

    public static String getIdentifier() {
        return OstDeviceManager.ADDRESS;
    }

    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }


    private static EntityFactory entityFactory;
    private static EntityFactory getEntityFactory() {
        if ( null == entityFactory ) {
            entityFactory = new EntityFactory() {
                @Override
                public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                    return new OstDeviceManager(jsonObject);
                }
            };
        }
        return entityFactory;
    }

    public static OstDeviceManager parse(JSONObject jsonObject) throws JSONException {
        return (OstDeviceManager) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getDeviceManagerModel(), getIdentifier(), getEntityFactory());
    }


    @Override
    protected OstDeviceManager updateWithJsonObject(JSONObject jsonObject) throws JSONException {
        return OstDeviceManager.parse(jsonObject);
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

    public String getUserId() {
        return this.getParentId();
    }

    public String getAddress() {
        return this.getId();
    }

    public int getRequirement() {
        JSONObject jsonObject = this.getJSONData();
        if ( null == jsonObject ) {
            Log.e(TAG, "getRequirement: jsonObject is null");
            return 0;
        }
        return jsonObject.optInt(OstDeviceManager.REQUIREMENT, 0);
    }


    public String getNonce() {
        return getJSONData().optString(OstDeviceManager.NONCE, null);
    }


    @Override
    public String getParentIdKey() {
        return OstDeviceManager.USER_ID;
    }
}