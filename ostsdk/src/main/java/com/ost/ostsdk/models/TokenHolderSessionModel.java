package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.TokenHolderSession;
import com.ost.ostsdk.models.entities.User;

import org.json.JSONException;
import org.json.JSONObject;

public interface TokenHolderSessionModel {

    void insertTokenHolderSession(TokenHolderSession tokenHolderSession, TaskCallback callback);

    void insertAllTokenHolderSessions(TokenHolderSession[] tokenHolderSession, TaskCallback callback);

    void deleteTokenHolderSession(TokenHolderSession tokenHolderSession, TaskCallback callback);

    TokenHolderSession[] getTokenHolderSessionsByIds(String[] ids);

    TokenHolderSession getTokenHolderSessionById(String id);

    void deleteAllTokenHolderSessions(TaskCallback callback);

    TokenHolderSession initTokenHolderSession(JSONObject jsonObject) throws JSONException;

}