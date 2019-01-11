package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.models.entities.Rule;

@Dao
public abstract class RuleDao implements BaseDao {

    public void insert(BaseEntity baseEntity) {
        this.insert((Rule) baseEntity);
    }

    public void insertAll(BaseEntity... baseEntity) {
        this.insertAll((Rule[]) baseEntity);
    }

    @Insert
    public abstract void insert(Rule rule);

    @Insert
    public abstract void insertAll(Rule... rule);

    @Query("DELETE FROM Rule WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM Rule WHERE id IN (:ids)")
    public abstract Rule[] getByIds(String[] ids);

    @Query("SELECT * FROM Rule WHERE id=:id")
    public abstract Rule getById(String id);

    @Query("DELETE FROM Rule")
    public abstract void deleteAll();

    @Query("SELECT * FROM rule WHERE parent_id=:id")
    public abstract Rule[] getByParentId(String id);
}
