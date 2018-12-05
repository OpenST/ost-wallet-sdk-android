package com.ost.ostsdk.data.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.data.models.entities.UserEntity;

@Dao
public interface UserDao {

    @Insert
    void insert(UserEntity userEntity);

    @Insert
    void insertAll(UserEntity... userEntity);

    @Delete
    void delete(UserEntity userEntity);

    @Query("SELECT * FROM user WHERE id IN (:ids)")
    UserEntity getByIds(double[] ids);

    @Query("SELECT * FROM user WHERE id=:id")
    UserEntity getById(double id);

    @Query("DELETE FROM user")
    void deleteAll();
}
