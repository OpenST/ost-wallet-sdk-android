package com.ost.mobilesdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.mobilesdk.models.entities.OstSessionKey;


@Dao
public interface OstSessionKeyDao {

    @Insert
    void insert(OstSessionKey ostSessionKey);

    @Insert
    void insertAll(OstSessionKey... ostSessionKey);

    @Query("DELETE FROM session_key WHERE `key`=:key")
    void delete(String key);

    @Query("SELECT * FROM session_key WHERE `key` IN (:keys)")
    OstSessionKey[] getByIds(String[] keys);

    @Query("SELECT * FROM session_key WHERE `key`=:key")
    OstSessionKey getById(String key);

    @Query("DELETE FROM session_key")
    void deleteAll();
}