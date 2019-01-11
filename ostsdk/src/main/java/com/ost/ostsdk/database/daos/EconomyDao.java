package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.models.entities.Economy;

@Dao
public abstract class EconomyDao implements BaseDao {

    public void insert(BaseEntity baseEntity) {
        this.insert((Economy) baseEntity);
    }

    public void insertAll(BaseEntity... baseEntity) {
        this.insertAll((Economy[]) baseEntity);
    }

    @Insert
    public abstract void insert(Economy economy);

    @Insert
    public abstract void insertAll(Economy... economy);

    @Query("DELETE FROM economy WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM economy WHERE id IN (:ids)")
    public abstract Economy[] getByIds(String[] ids);

    @Query("SELECT * FROM economy WHERE id=:id")
    public abstract Economy getById(String id);

    @Query("DELETE FROM economy")
    public abstract void deleteAll();

    @Query("SELECT * FROM economy WHERE parent_id=:id")
    public abstract Economy[] getByParentId(String id);
}
