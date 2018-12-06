package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.TokenHolderSession;

@Dao
public interface TokenHolderSessionDao {
    @Insert
    void insert(TokenHolderSession tokenHolderSession);

    @Insert
    void insertAll(TokenHolderSession... tokenHolderSession);

    @Delete
    void delete(TokenHolderSession tokenHolderSession);

    @Query("SELECT * FROM token_holder_session WHERE id IN (:ids)")
    TokenHolderSession getByIds(double[] ids);

    @Query("SELECT * FROM token_holder_session WHERE id=:id")
    TokenHolderSession getById(double id);

    @Query("DELETE FROM token_holder_session")
    void deleteAll();
}
