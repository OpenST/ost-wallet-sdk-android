package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.OstBaseEntity;
import com.ost.ostsdk.models.entities.OstRule;

@Dao
public abstract class OstRuleDao implements OstBaseDao {

    public void insert(OstBaseEntity baseEntity) {
        this.insert((OstRule) baseEntity);
    }

    public void insertAll(OstBaseEntity... baseEntity) {
        this.insertAll((OstRule[]) baseEntity);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(OstRule ostRule);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(OstRule... ostRule);

    @Query("DELETE FROM rule WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM rule WHERE id IN (:ids)")
    public abstract OstRule[] getByIds(String[] ids);

    @Query("SELECT * FROM rule WHERE id=:id")
    public abstract OstRule getById(String id);

    @Query("DELETE FROM rule")
    public abstract void deleteAll();

    @Query("SELECT * FROM rule WHERE parent_id=:id")
    public abstract OstRule[] getByParentId(String id);
}
