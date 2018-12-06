package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.User;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Insert
    void insertAll(User... user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM User WHERE id IN (:ids)")
    User getByIds(double[] ids);

    @Query("SELECT * FROM User WHERE id=:id")
    User getById(double id);

    @Query("DELETE FROM User")
    void deleteAll();
}
