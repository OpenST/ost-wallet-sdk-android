package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.OstBaseEntity;
import com.ost.ostsdk.models.entities.OstCredits;
import com.ost.ostsdk.models.entities.OstTokenHolder;

@Dao
public abstract class OstCreditsDao implements OstBaseDao {

    public void insert(OstBaseEntity baseEntity) {
        this.insert((OstCredits) baseEntity);
    }

    public void insertAll(OstBaseEntity... baseEntity) {
        this.insertAll((OstCredits[]) baseEntity);
    }

    @Insert
    public abstract void insert(OstCredits ostCredits);

    @Insert
    public abstract void insertAll(OstCredits... ostCredits);

    @Query("DELETE FROM credits WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM credits WHERE id IN (:ids)")
    public abstract OstTokenHolder[] getByIds(String[] ids);

    @Query("SELECT * FROM credits WHERE id=:id")
    public abstract OstTokenHolder getById(String id);

    @Query("DELETE FROM credits")
    public abstract void deleteAll();

    @Query("SELECT * FROM credits WHERE parent_id=:id")
    public abstract OstTokenHolder[] getByParentId(String id);
}