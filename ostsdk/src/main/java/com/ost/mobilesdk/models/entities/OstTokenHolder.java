package com.ost.mobilesdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.text.TextUtils;

import com.ost.mobilesdk.models.Impls.OstModelFactory;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Keys;


@Entity(tableName = "token_holder")
public class OstTokenHolder extends OstBaseEntity {

    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";

    public static String getIdentifier() {
        return OstTokenHolder.ADDRESS;
    }

    public static class CONST_STATUS {
        public static final String INITIALIZING = "initializing";
        public static final String ACTIVATED = "activated";
    }

    public OstTokenHolder getById(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        id = Keys.toChecksumAddress(id);
        return OstModelFactory.getTokenHolderModel().getEntityById(id);
    }
    private static EntityFactory entityFactory;
    private static EntityFactory getEntityFactory() {
        if ( null == entityFactory ) {
            entityFactory = new EntityFactory() {
                @Override
                public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                    return new OstTokenHolder(jsonObject);
                }
            };
        }
        return entityFactory;
    }

    public static OstTokenHolder parse(JSONObject jsonObject) throws JSONException {
        return (OstTokenHolder) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getTokenHolderModel(), getIdentifier(), getEntityFactory());
    }


    @Override
    protected OstTokenHolder updateWithJsonObject(JSONObject jsonObject) throws JSONException {
        return OstTokenHolder.parse(jsonObject);
    }

    public OstTokenHolder(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstTokenHolder(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstTokenHolder.USER_ID) &&
                jsonObject.has(OstTokenHolder.ADDRESS);


    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
    }

    public String getUserId() {
        return this.getParentId();
    }

    @Override
    public String getId() {
        String id = super.getId();
        id = Keys.toChecksumAddress(id);
        return id;
    }

    public String getAddress() {
        return this.getId();
    }


    public OstSession getDeviceTokenHolderSession() throws Exception {
        OstSession deviceSession = null;
        OstSession sessions[] = OstModelFactory.getSessionModel().getEntitiesByParentId(getId());
        for (OstSession session : sessions) {
            if (null != new OstSecureKeyModelRepository().getByKey(session.getAddress())) {
                deviceSession = session;
                break;
            }
        }
        if (null == deviceSession) {
            throw new Exception("Wallet not found in db");
        }
        return deviceSession;
    }

    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }

    @Override
    public String getParentIdKey() {
        return OstDevice.USER_ID;
    }
}
