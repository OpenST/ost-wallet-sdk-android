package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstSession;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstSessionModel {

    void insertTokenHolderSession(OstSession ostSession, OstTaskCallback callback);

    void insertAllTokenHolderSessions(OstSession[] ostSession, OstTaskCallback callback);

    void deleteTokenHolderSession(String id, OstTaskCallback callback);

    OstSession[] getTokenHolderSessionsByIds(String[] ids);

    OstSession getTokenHolderSessionById(String id);

    void deleteAllTokenHolderSessions(OstTaskCallback callback);

    OstSession initTokenHolderSession(JSONObject jsonObject, OstTaskCallback callback) throws JSONException;

    OstSession[] getTokenHolderSessionsByParentId(String id);
}