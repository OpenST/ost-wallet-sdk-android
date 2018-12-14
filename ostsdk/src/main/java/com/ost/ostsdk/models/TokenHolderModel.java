package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.TokenHolder;

import org.json.JSONException;
import org.json.JSONObject;

public interface TokenHolderModel {

    void insertTokenHolder(TokenHolder tokenHolder, TaskCallback callback);

    void insertAllTokenHolders(TokenHolder[] tokenHolders, TaskCallback callback);

    void deleteTokenHolder(String id, TaskCallback callback);

    TokenHolder[] getTokenHoldersByIds(String[] ids);

    TokenHolder getTokenHolderById(String id);

    void deleteAllTokenHolders(TaskCallback callback);

    TokenHolder initTokenHolder(JSONObject jsonObject, TaskCallback callback) throws JSONException;
}