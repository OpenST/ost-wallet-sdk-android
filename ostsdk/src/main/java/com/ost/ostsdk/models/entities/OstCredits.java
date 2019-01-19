package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.models.Impls.OstModelFactory;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "credits")
public class OstCredits extends OstBaseEntity {

    public static final String AMOUNT = "token_id";
    public static final String USER_IDS = "address";
    public static final String STEP_COMPLETE = "step_complete";

    public static String getIdentifier() {
        return OstCredits.ID;
    }

    public static OstCredits parse(JSONObject jsonObject) throws JSONException {
        return (OstCredits) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getCreditsModel(), getIdentifier(), new EntityFactory() {
            @Override
            public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                return new OstCredits(jsonObject);
            }
        });
    }

    public OstCredits(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstCredits(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstCredits.AMOUNT) &&
                jsonObject.has(OstCredits.STEP_COMPLETE) &&
                jsonObject.has(OstCredits.USER_IDS);


    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
    }

    public String getAmount() {
        return getJSONData().optString(OstCredits.AMOUNT, null);
    }

    public String getUserIds() {
        return getJSONData().optString(OstCredits.USER_IDS, null);
    }

    public String getStepComplete() {
        return getJSONData().optString(OstCredits.STEP_COMPLETE, null);
    }


    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }

    @Override
    public String getParentIdKey() {
        return OstCredits.PARENT_ID;
    }
}