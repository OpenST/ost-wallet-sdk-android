package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.ExecutableRule;

@Dao
public interface ExecutableRuleDao {
    @Insert
    void insert(ExecutableRule executableRule);

    @Insert
    void insertAll(ExecutableRule... executableRule);

    @Delete
    void delete(ExecutableRule executableRule);

    @Query("SELECT * FROM executable_rule WHERE id IN (:ids)")
    ExecutableRule getByIds(double[] ids);

    @Query("SELECT * FROM executable_rule WHERE id=:id")
    ExecutableRule getById(double id);

    @Query("DELETE FROM executable_rule")
    void deleteAll();
}
