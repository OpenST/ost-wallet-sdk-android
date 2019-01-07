package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.SecureKey;

@Dao
public interface SecureKeyDao {

    @Insert
    void insert(SecureKey secureKey);

    @Insert
    void insertAll(SecureKey... secureKey);

    @Query("DELETE FROM secure_key WHERE `key`=:key")
    void delete(String key);

    @Query("SELECT * FROM secure_key WHERE `key` IN (:keys)")
    SecureKey[] getByIds(String[] keys);

    @Query("SELECT * FROM secure_key WHERE `key`=:key")
    SecureKey getById(String key);

    @Query("DELETE FROM secure_key")
    void deleteAll();
}