package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
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

    public void delete(BaseEntity baseEntity) {
        this.delete((Economy) baseEntity);
    }

    @Insert
    public abstract void insert(Economy economy);

    @Insert
    public abstract void insertAll(Economy... economy);

    @Delete
    public abstract void delete(Economy economy);

    @Query("SELECT * FROM Economy WHERE id IN (:ids)")
    public abstract Economy[] getByIds(String[] ids);

    @Query("SELECT * FROM Economy WHERE id=:id")
    public abstract Economy getById(String id);

    @Query("DELETE FROM Economy")
    public abstract void deleteAll();
}
