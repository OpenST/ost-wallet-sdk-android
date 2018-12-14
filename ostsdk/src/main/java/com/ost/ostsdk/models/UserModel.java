package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.User;

import org.json.JSONException;
import org.json.JSONObject;

public interface UserModel {

    void insertAllUsers(User[] user, TaskCallback callback);

    void deleteUser(String id, TaskCallback callback);

    User[] getUsersByIds(String[] ids);

    User getUserById(String id);

    void deleteAllUsers(TaskCallback callback);

    User initUser(JSONObject jsonObject, TaskCallback callback) throws JSONException;

    User initUser(JSONObject jsonObject) throws JSONException;

}
