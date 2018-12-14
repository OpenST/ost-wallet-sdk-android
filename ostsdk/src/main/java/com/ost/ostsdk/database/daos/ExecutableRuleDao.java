package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.models.entities.ExecutableRule;

@Dao
public abstract class ExecutableRuleDao implements BaseDao {

    public void insert(BaseEntity baseEntity) {
        this.insert((ExecutableRule) baseEntity);
    }

    public void insertAll(BaseEntity... baseEntity) {
        this.insertAll((ExecutableRule[]) baseEntity);
    }

    @Insert
    public abstract void insert(ExecutableRule executableRule);

    @Insert
    public abstract void insertAll(ExecutableRule... executableRule);

    @Query("DELETE FROM executable_rule WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM executable_rule WHERE id IN (:ids)")
    public abstract ExecutableRule[] getByIds(String[] ids);

    @Query("SELECT * FROM executable_rule WHERE id=:id")
    public abstract ExecutableRule getById(String id);

    @Query("DELETE FROM executable_rule")
    public abstract void deleteAll();
}
