package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstUser;

import org.json.JSONException;
import org.json.JSONObject;

public interface UserModel {

    void insertAllUsers(OstUser[] ostUser, TaskCallback callback);

    void deleteUser(String id, TaskCallback callback);

    OstUser[] getUsersByIds(String[] ids);

    OstUser getUserById(String id);

    void deleteAllUsers(TaskCallback callback);

    OstUser initUser(JSONObject jsonObject, TaskCallback callback) throws JSONException;

    OstUser initUser(JSONObject jsonObject) throws JSONException;

    OstUser update(OstUser ostUser, TaskCallback callback);
}
