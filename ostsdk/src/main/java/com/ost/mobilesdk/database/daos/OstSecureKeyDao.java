package com.ost.mobilesdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ost.mobilesdk.models.entities.OstSecureKey;

@Dao
public interface OstSecureKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OstSecureKey ostSecureKey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(OstSecureKey... ostSecureKey);

    @Query("DELETE FROM bytes_storage WHERE `id`=:id")
    void delete(String id);

    @Query("SELECT * FROM bytes_storage WHERE `id` IN (:id)")
    OstSecureKey[] getByIds(String[] id);

    @Query("SELECT * FROM bytes_storage WHERE `id`=:id")
    OstSecureKey getById(String id);

    @Query("DELETE FROM bytes_storage")
    void deleteAll();
}