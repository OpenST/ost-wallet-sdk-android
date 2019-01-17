package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstSession;

public interface OstSessionModel {

    void insertTokenHolderSession(OstSession ostSession);

    void insertAllTokenHolderSessions(OstSession[] ostSession);

    void deleteTokenHolderSession(String id);

    OstSession[] getTokenHolderSessionsByIds(String[] ids);

    OstSession getTokenHolderSessionById(String id);

    void deleteAllTokenHolderSessions();

    OstSession insert(OstSession ostSession);

    OstSession[] getTokenHolderSessionsByParentId(String id);
}