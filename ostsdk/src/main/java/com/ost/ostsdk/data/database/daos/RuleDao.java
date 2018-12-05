package com.ost.ostsdk.data.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.data.models.entities.RuleEntity;

@Dao
public interface RuleDao {

    @Insert
    void insert(RuleEntity ruleEntity);

    @Insert
    void insertAll(RuleEntity... ruleEntity);

    @Delete
    void delete(RuleEntity ruleEntity);

    @Query("SELECT * FROM rule WHERE id IN (:ids)")
    RuleEntity getByIds(double[] ids);

    @Query("SELECT * FROM rule WHERE id=:id")
    RuleEntity getById(double id);

    @Query("DELETE FROM rule")
    void deleteAll();
}
