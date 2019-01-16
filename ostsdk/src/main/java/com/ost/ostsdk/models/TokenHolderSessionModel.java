package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstSession;

import org.json.JSONException;
import org.json.JSONObject;

public interface TokenHolderSessionModel {

    void insertTokenHolderSession(OstSession ostSession, TaskCallback callback);

    void insertAllTokenHolderSessions(OstSession[] ostSession, TaskCallback callback);

    void deleteTokenHolderSession(String id, TaskCallback callback);

    OstSession[] getTokenHolderSessionsByIds(String[] ids);

    OstSession getTokenHolderSessionById(String id);

    void deleteAllTokenHolderSessions(TaskCallback callback);

    OstSession initTokenHolderSession(JSONObject jsonObject, TaskCallback callback) throws JSONException;

    OstSession[] getTokenHolderSessionsByParentId(String id);
}