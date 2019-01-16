package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.OstBaseEntity;
import com.ost.ostsdk.models.entities.OstUser;

@Dao
public abstract class OstUserDao implements OstBaseDao {

    public void insert(OstBaseEntity baseEntity) {
        this.insert((OstUser) baseEntity);
    }

    public void insertAll(OstBaseEntity... baseEntity) {
        this.insertAll((OstUser[]) baseEntity);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(OstUser ostUser);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(OstUser... ostUser);

    @Query("DELETE FROM user WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM user WHERE id IN (:ids)")
    public abstract OstUser[] getByIds(String[] ids);

    @Query("SELECT * FROM user WHERE id=:id")
    public abstract OstUser getById(String id);

    @Query("DELETE FROM user")
    public abstract void deleteAll();

    @Query("SELECT * FROM user WHERE parent_id=:id")
    public abstract OstUser[] getByParentId(String id);
}