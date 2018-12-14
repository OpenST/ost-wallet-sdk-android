package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.models.entities.SecureKey;

@Dao
public abstract class SecureKeyDao implements BaseDao {

    public void insert(BaseEntity baseEntity) {
        this.insert((SecureKey) baseEntity);
    }

    public void insertAll(BaseEntity... baseEntity) {
        this.insertAll((SecureKey[]) baseEntity);
    }

    @Insert
    public abstract void insert(SecureKey secureKey);

    @Insert
    public abstract void insertAll(SecureKey... secureKey);

    @Query("DELETE FROM secure_key WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM secure_key WHERE id IN (:ids)")
    public abstract SecureKey[] getByIds(String[] ids);

    @Query("SELECT * FROM secure_key WHERE id=:id")
    public abstract SecureKey getById(String id);

    @Query("DELETE FROM secure_key")
    public abstract void deleteAll();
}