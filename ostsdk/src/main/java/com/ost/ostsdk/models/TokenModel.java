package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstToken;

import org.json.JSONException;
import org.json.JSONObject;

public interface TokenModel {

    OstToken registerToken(JSONObject jsonObject, TaskCallback callback) throws JSONException;

    OstToken getTokenById(String id);
}