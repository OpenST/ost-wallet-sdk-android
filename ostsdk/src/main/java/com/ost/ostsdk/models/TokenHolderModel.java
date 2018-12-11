package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.TokenHolder;

import org.json.JSONObject;

public interface TokenHolderModel {

    void insertTokenHolder(TokenHolder tokenHolder, TaskCompleteCallback callback);

    void insertAllTokenHolders(TokenHolder[] tokenHolder, TaskCompleteCallback callback);

    void deleteTokenHolder(TokenHolder tokenHolder, TaskCompleteCallback callback);

    TokenHolder[] getTokenHoldersByIds(String[] ids);

    TokenHolder getTokenHolderById(String id);

    void deleteAllTokenHolders(TaskCompleteCallback callback);

    TokenHolder initTokenHolder(JSONObject jsonObject);
}