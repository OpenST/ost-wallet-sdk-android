package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstTokenHolder;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstTokenHolderModel {

    void insertTokenHolder(OstTokenHolder ostTokenHolder, OstTaskCallback callback);

    void insertAllTokenHolders(OstTokenHolder[] ostTokenHolders, OstTaskCallback callback);

    void deleteTokenHolder(String id, OstTaskCallback callback);

    OstTokenHolder[] getTokenHoldersByIds(String[] ids);

    OstTokenHolder getTokenHolderById(String id);

    void deleteAllTokenHolders(OstTaskCallback callback);

    OstTokenHolder initTokenHolder(JSONObject jsonObject, OstTaskCallback callback) throws JSONException;
}