package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstTokenHolderSession;

import org.json.JSONException;
import org.json.JSONObject;

public interface TokenHolderSessionModel {

    void insertTokenHolderSession(OstTokenHolderSession ostTokenHolderSession, TaskCallback callback);

    void insertAllTokenHolderSessions(OstTokenHolderSession[] ostTokenHolderSession, TaskCallback callback);

    void deleteTokenHolderSession(String id, TaskCallback callback);

    OstTokenHolderSession[] getTokenHolderSessionsByIds(String[] ids);

    OstTokenHolderSession getTokenHolderSessionById(String id);

    void deleteAllTokenHolderSessions(TaskCallback callback);

    OstTokenHolderSession initTokenHolderSession(JSONObject jsonObject, TaskCallback callback) throws JSONException;

    OstTokenHolderSession[] getTokenHolderSessionsByParentId(String id);
}