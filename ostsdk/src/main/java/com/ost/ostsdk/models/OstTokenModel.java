package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstToken;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstTokenModel {

    OstToken registerToken(JSONObject jsonObject, OstTaskCallback callback) throws JSONException;

    OstToken getTokenById(String id);
}