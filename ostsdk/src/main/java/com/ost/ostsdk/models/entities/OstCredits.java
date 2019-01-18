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


    public static OstCredits parse(JSONObject jsonObject) throws JSONException {
        OstCredits ostCredits = new OstCredits(jsonObject);
        return OstModelFactory.getOstCreditsModel().insert(ostCredits);
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
        return getData().optString(OstCredits.AMOUNT, null);
    }

    public String getUserIds() {
        return getData().optString(OstCredits.USER_IDS, null);
    }

    public String getStepComplete() {
        return getData().optString(OstCredits.STEP_COMPLETE, null);
    }


    @Override
    String getEntityIdKey() {
        return OstCredits.ID;
    }

    @Override
    public String getParentIdKey() {
        return OstCredits.PARENT_ID;
    }
}