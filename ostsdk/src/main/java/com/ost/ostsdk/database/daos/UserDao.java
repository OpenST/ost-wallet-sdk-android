package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.models.entities.User;

@Dao
public abstract class UserDao implements BaseDao {

    public void insert(BaseEntity baseEntity) {
        this.insert((User) baseEntity);
    }

    public void insertAll(BaseEntity... baseEntity) {
        this.insertAll((User[]) baseEntity);
    }

    @Insert
    public abstract void insert(User user);

    @Insert
    public abstract void insertAll(User... user);

    @Query("DELETE FROM User WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM User WHERE id IN (:ids)")
    public abstract User[] getByIds(String[] ids);

    @Query("SELECT * FROM User WHERE id=:id")
    public abstract User getById(String id);

    @Query("DELETE FROM User")
    public abstract void deleteAll();
}