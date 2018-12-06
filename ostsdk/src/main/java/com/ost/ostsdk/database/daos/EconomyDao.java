package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.Economy;

@Dao
public interface EconomyDao {
    @Insert
    void insert(Economy economy);

    @Insert
    void insertAll(Economy... economy);

    @Delete
    void delete(Economy economy);

    @Query("SELECT * FROM Economy WHERE id IN (:ids)")
    Economy getByIds(double[] ids);

    @Query("SELECT * FROM Economy WHERE id=:id")
    Economy getById(double id);

    @Query("DELETE FROM Economy")
    void deleteAll();
}
