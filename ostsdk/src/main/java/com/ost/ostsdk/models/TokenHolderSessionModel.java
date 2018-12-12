package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.TokenHolderSession;

import org.json.JSONException;
import org.json.JSONObject;

public interface TokenHolderSessionModel {

    void insertTokenHolderSession(TokenHolderSession tokenHolderSession, TaskCompleteCallback callback);

    void insertAllTokenHolderSessions(TokenHolderSession[] tokenHolderSession, TaskCompleteCallback callback);

    void deleteTokenHolderSession(TokenHolderSession tokenHolderSession, TaskCompleteCallback callback);

    TokenHolderSession[] getTokenHolderSessionsByIds(String[] ids);

    TokenHolderSession getTokenHolderSessionById(String id);

    void deleteAllTokenHolderSessions(TaskCompleteCallback callback);

    TokenHolderSession initTokenHolderSession(JSONObject jsonObject) throws JSONException;
}