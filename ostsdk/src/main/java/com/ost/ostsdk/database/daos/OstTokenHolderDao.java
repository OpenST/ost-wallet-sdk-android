package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.OstBaseEntity;
import com.ost.ostsdk.models.entities.OstTokenHolder;

@Dao
public abstract class OstTokenHolderDao implements OstBaseDao {

    public void insert(OstBaseEntity baseEntity) {
        this.insert((OstTokenHolder) baseEntity);
    }

    public void insertAll(OstBaseEntity... baseEntity) {
        this.insertAll((OstTokenHolder[]) baseEntity);
    }

    @Insert
    public abstract void insert(OstTokenHolder ostTokenHolder);

    @Insert
    public abstract void insertAll(OstTokenHolder... ostTokenHolder);

    @Query("DELETE FROM token_holder WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM token_holder WHERE id IN (:ids)")
    public abstract OstTokenHolder[] getByIds(String[] ids);

    @Query("SELECT * FROM token_holder WHERE id=:id")
    public abstract OstTokenHolder getById(String id);

    @Query("DELETE FROM token_holder")
    public abstract void deleteAll();

    @Query("SELECT * FROM token_holder WHERE parent_id=:id")
    public abstract OstTokenHolder[] getByParentId(String id);
}