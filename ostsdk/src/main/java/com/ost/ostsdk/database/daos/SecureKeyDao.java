package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.SecureKey;

@Dao
public interface SecureKeyDao {
    @Insert
    void insert(SecureKey secureKey);

    @Insert
    void insertAll(SecureKey... secureKey);

    @Delete
    void delete(SecureKey secureKey);

    @Query("SELECT * FROM secure_key WHERE id IN (:ids)")
    SecureKey getByIds(double[] ids);

    @Query("SELECT * FROM secure_key WHERE id=:id")
    SecureKey getById(double id);

    @Query("DELETE FROM secure_key")
    void deleteAll();
}
