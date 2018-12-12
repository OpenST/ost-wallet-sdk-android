package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.TokenHolder;

import org.json.JSONException;
import org.json.JSONObject;

public interface TokenHolderModel {

    void insertTokenHolder(TokenHolder tokenHolder, TaskCompleteCallback callback);

    void insertAllTokenHolders(TokenHolder[] tokenHolders, TaskCompleteCallback callback);

    void deleteTokenHolder(TokenHolder tokenHolder, TaskCompleteCallback callback);

    TokenHolder[] getTokenHoldersByIds(String[] ids);

    TokenHolder getTokenHolderById(String id);

    void deleteAllTokenHolders(TaskCompleteCallback callback);

    TokenHolder initTokenHolder(JSONObject jsonObject) throws JSONException;
}