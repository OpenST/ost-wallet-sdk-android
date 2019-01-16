package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstUser;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstUserModel {

    void insertAllUsers(OstUser[] ostUser);

    void deleteUser(String id);

    OstUser[] getUsersByIds(String[] ids);

    OstUser getUserById(String id);

    void deleteAllUsers();

    OstUser initUser(JSONObject jsonObject) throws JSONException;

    OstUser update(OstUser ostUser);
}
