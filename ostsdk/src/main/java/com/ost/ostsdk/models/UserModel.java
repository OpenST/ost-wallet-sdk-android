package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.User;

import org.json.JSONObject;

public interface UserModel {

    void insertUser(User user, TaskCompleteCallback callback);

    void insertAllUsers(User[] user, TaskCompleteCallback callback);

    void deleteUser(User user, TaskCompleteCallback callback);

    User getUsersByIds(double[] ids);

    User getUserById(double id);

    void deleteAllUsers(TaskCompleteCallback callback);

    User initUser(JSONObject jsonObject);
}
