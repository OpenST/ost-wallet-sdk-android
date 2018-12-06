package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.TokenHolder;

@Dao
public interface TokenHolderDao {
    @Insert
    void insert(TokenHolder tokenHolder);

    @Insert
    void insertAll(TokenHolder... tokenHolder);

    @Delete
    void delete(TokenHolder tokenHolder);

    @Query("SELECT * FROM token_holder WHERE id IN (:ids)")
    TokenHolder getByIds(double[] ids);

    @Query("SELECT * FROM token_holder WHERE id=:id")
    TokenHolder getById(double id);

    @Query("DELETE FROM token_holder")
    void deleteAll();
}
