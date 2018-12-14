package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.models.entities.TokenHolderSession;

@Dao
public abstract class TokenHolderSessionDao implements BaseDao {

    public void insert(BaseEntity baseEntity) {
        this.insert((TokenHolderSession) baseEntity);
    }

    public void insertAll(BaseEntity... baseEntity) {
        this.insertAll((TokenHolderSession[]) baseEntity);
    }

    @Insert
    public abstract void insert(TokenHolderSession tokenHolderSession);

    @Insert
    public abstract void insertAll(TokenHolderSession... tokenHolderSession);

    @Query("DELETE FROM token_holder_session WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM token_holder_session WHERE id IN (:ids)")
    public abstract TokenHolderSession[] getByIds(String[] ids);

    @Query("SELECT * FROM token_holder_session WHERE id=:id")
    public abstract TokenHolderSession getById(String id);

    @Query("DELETE FROM token_holder_session")
    public abstract void deleteAll();
}
