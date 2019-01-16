package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstTokenHolder;

import org.json.JSONException;
import org.json.JSONObject;

public interface TokenHolderModel {

    void insertTokenHolder(OstTokenHolder ostTokenHolder, TaskCallback callback);

    void insertAllTokenHolders(OstTokenHolder[] ostTokenHolders, TaskCallback callback);

    void deleteTokenHolder(String id, TaskCallback callback);

    OstTokenHolder[] getTokenHoldersByIds(String[] ids);

    OstTokenHolder getTokenHolderById(String id);

    void deleteAllTokenHolders(TaskCallback callback);

    OstTokenHolder initTokenHolder(JSONObject jsonObject, TaskCallback callback) throws JSONException;
}