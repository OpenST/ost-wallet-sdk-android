package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.Rule;

@Dao
public interface RuleDao {

    @Insert
    void insert(Rule rule);

    @Insert
    void insertAll(Rule... rule);

    @Delete
    void delete(Rule rule);

    @Query("SELECT * FROM Rule WHERE id IN (:ids)")
    Rule getByIds(double[] ids);

    @Query("SELECT * FROM Rule WHERE id=:id")
    Rule getById(double id);

    @Query("DELETE FROM Rule")
    void deleteAll();
}
