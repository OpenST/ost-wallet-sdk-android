package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.Token;

import org.json.JSONException;
import org.json.JSONObject;

public interface TokenModel {

    Token registerToken(JSONObject jsonObject, TaskCallback callback) throws JSONException;

    Token getTokenById(String id);
}