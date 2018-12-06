package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.User;

import org.json.JSONObject;

public interface UserModel {

    void insertUser(User user, DbProcessCallback callback);

    void insertAllUsers(User[] user, DbProcessCallback callback);

    void deleteUser(User user, DbProcessCallback callback);

    User getUsersByIds(double[] ids);

    User getUserById(double id);

    void deleteAllUsers(DbProcessCallback callback);

    User initUser(JSONObject jsonObject);
}
