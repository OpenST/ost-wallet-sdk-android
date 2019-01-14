package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.TokenHolderSession;

import org.json.JSONException;
import org.json.JSONObject;

public interface TokenHolderSessionModel {

    void insertTokenHolderSession(TokenHolderSession tokenHolderSession, TaskCallback callback);

    void insertAllTokenHolderSessions(TokenHolderSession[] tokenHolderSession, TaskCallback callback);

    void deleteTokenHolderSession(String id, TaskCallback callback);

    TokenHolderSession[] getTokenHolderSessionsByIds(String[] ids);

    TokenHolderSession getTokenHolderSessionById(String id);

    void deleteAllTokenHolderSessions(TaskCallback callback);

    TokenHolderSession initTokenHolderSession(JSONObject jsonObject, TaskCallback callback) throws JSONException;

    TokenHolderSession[] getTokenHolderSessionsByParentId(String id);
}