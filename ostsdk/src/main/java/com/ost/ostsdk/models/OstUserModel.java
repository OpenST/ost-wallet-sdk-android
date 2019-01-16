package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstUser;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstUserModel {

    void insertAllUsers(OstUser[] ostUser, OstTaskCallback callback);

    void deleteUser(String id, OstTaskCallback callback);

    OstUser[] getUsersByIds(String[] ids);

    OstUser getUserById(String id);

    void deleteAllUsers(OstTaskCallback callback);

    OstUser initUser(JSONObject jsonObject, OstTaskCallback callback) throws JSONException;

    OstUser initUser(JSONObject jsonObject) throws JSONException;

    OstUser update(OstUser ostUser, OstTaskCallback callback);
}
