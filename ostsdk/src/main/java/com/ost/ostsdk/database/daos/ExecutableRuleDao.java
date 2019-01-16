package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.OstBaseEntity;
import com.ost.ostsdk.models.entities.OstExecutableRule;

@Dao
public abstract class ExecutableRuleDao implements BaseDao {

    public void insert(OstBaseEntity baseEntity) {
        this.insert((OstExecutableRule) baseEntity);
    }

    public void insertAll(OstBaseEntity... baseEntity) {
        this.insertAll((OstExecutableRule[]) baseEntity);
    }

    @Insert
    public abstract void insert(OstExecutableRule executableRule);

    @Insert
    public abstract void insertAll(OstExecutableRule... executableRule);

    @Query("DELETE FROM executable_rule WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM executable_rule WHERE id IN (:ids)")
    public abstract OstExecutableRule[] getByIds(String[] ids);

    @Query("SELECT * FROM executable_rule WHERE id=:id")
    public abstract OstExecutableRule getById(String id);

    @Query("DELETE FROM executable_rule")
    public abstract void deleteAll();

    @Query("SELECT * FROM executable_rule WHERE parent_id=:id")
    public abstract OstExecutableRule[] getByParentId(String id);
}
