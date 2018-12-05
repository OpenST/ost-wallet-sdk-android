package com.ost.ostsdk.data.models;

import com.ost.ostsdk.data.models.entities.UserEntity;

public interface UserModel {

    void insertUser(UserEntity userEntity, DbProcessCallback callback);

    void insertAllUsers(UserEntity[] userEntity, DbProcessCallback callback);

    void deleteUser(UserEntity userEntity, DbProcessCallback callback);

    UserEntity getUsersByIds(double[] ids);

    UserEntity getUserById(double id);

    void deleteAllUsers(DbProcessCallback callback);
}
