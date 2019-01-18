package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.models.Impls.OstModelFactory;
import com.ost.ostsdk.models.Impls.OstSecureKeyModelRepository;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "token_holder")
public class OstTokenHolder extends OstBaseEntity {

    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";

    public static class CONST_STATUS {
        public static final String INITIALIZING = "INITIALIZING";
        public static final String ACTIVATED = "ACTIVATED";
    }

    public static OstTokenHolder parse(JSONObject jsonObject) throws JSONException {
        OstTokenHolder ostTokenHolder = new OstTokenHolder(jsonObject);
        return OstModelFactory.getTokenHolderModel().insert(ostTokenHolder);
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
        return getData().optString(OstTokenHolder.USER_ID, null);
    }

    public String getAddress() {
        return getData().optString(OstTokenHolder.ADDRESS, null);
    }


    public OstSession getDeviceTokenHolderSession() throws Exception {
        OstSession deviceSession = null;
        OstSession sessions[] = OstModelFactory.getSessionModel().getTokenHolderSessionsByParentId(getId());
        for (OstSession session : sessions) {
            if (null != new OstSecureKeyModelRepository().getById(session.getAddress())) {
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
        return OstTokenHolder.ID;
    }

    @Override
    public String getParentIdKey() {
        return OstTokenHolder.USER_ID;
    }
}
