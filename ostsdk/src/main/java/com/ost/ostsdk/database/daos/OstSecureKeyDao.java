package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.OstSecureKey;

@Dao
public interface OstSecureKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OstSecureKey ostSecureKey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(OstSecureKey... ostSecureKey);

    @Query("DELETE FROM secure_key WHERE `key`=:key")
    void delete(String key);

    @Query("SELECT * FROM secure_key WHERE `key` IN (:keys)")
    OstSecureKey[] getByIds(String[] keys);

    @Query("SELECT * FROM secure_key WHERE `key`=:key")
    OstSecureKey getById(String key);

    @Query("DELETE FROM secure_key")
    void deleteAll();
}