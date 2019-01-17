package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstTokenHolder;

public interface OstTokenHolderModel {

    void insertTokenHolder(OstTokenHolder ostTokenHolder);

    void insertAllTokenHolders(OstTokenHolder[] ostTokenHolders);

    void deleteTokenHolder(String id);

    OstTokenHolder[] getTokenHoldersByIds(String[] ids);

    OstTokenHolder getTokenHolderById(String id);

    void deleteAllTokenHolders();

    OstTokenHolder insert(OstTokenHolder ostTokenHolder);
}