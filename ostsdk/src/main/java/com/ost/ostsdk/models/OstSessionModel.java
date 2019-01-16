package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstSession;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstSessionModel {

    void insertTokenHolderSession(OstSession ostSession);

    void insertAllTokenHolderSessions(OstSession[] ostSession);

    void deleteTokenHolderSession(String id);

    OstSession[] getTokenHolderSessionsByIds(String[] ids);

    OstSession getTokenHolderSessionById(String id);

    void deleteAllTokenHolderSessions();

    OstSession initTokenHolderSession(JSONObject jsonObject) throws JSONException;

    OstSession[] getTokenHolderSessionsByParentId(String id);
}