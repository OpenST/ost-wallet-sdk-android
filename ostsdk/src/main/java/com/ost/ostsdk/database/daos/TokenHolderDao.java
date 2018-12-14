package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.models.entities.TokenHolder;

@Dao
public abstract class TokenHolderDao implements BaseDao {

    public void insert(BaseEntity baseEntity) {
        this.insert((TokenHolder) baseEntity);
    }

    public void insertAll(BaseEntity... baseEntity) {
        this.insertAll((TokenHolder[]) baseEntity);
    }

    @Insert
    public abstract void insert(TokenHolder tokenHolder);

    @Insert
    public abstract void insertAll(TokenHolder... tokenHolder);

    @Query("DELETE FROM token_holder WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM token_holder WHERE id IN (:ids)")
    public abstract TokenHolder[] getByIds(String[] ids);

    @Query("SELECT * FROM token_holder WHERE id=:id")
    public abstract TokenHolder getById(String id);

    @Query("DELETE FROM token_holder")
    public abstract void deleteAll();
}