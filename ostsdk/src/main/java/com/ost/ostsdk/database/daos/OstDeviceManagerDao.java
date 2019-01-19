package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.OstBaseEntity;
import com.ost.ostsdk.models.entities.OstDeviceManager;

@Dao
public abstract class OstDeviceManagerDao implements OstBaseDao {

    public void insert(OstBaseEntity baseEntity) {
        this.insert((OstDeviceManager) baseEntity);
    }

    public void insertAll(OstBaseEntity... baseEntity) {
        this.insertAll((OstDeviceManager[]) baseEntity);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(OstDeviceManager ostDeviceManager);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(OstDeviceManager... ostDeviceManager);

    @Query("DELETE FROM device_manager WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM device_manager WHERE id IN (:ids)")
    public abstract OstDeviceManager[] getByIds(String[] ids);

    @Query("SELECT * FROM device_manager WHERE id=:id")
    public abstract OstDeviceManager getById(String id);

    @Query("DELETE FROM device_manager")
    public abstract void deleteAll();

    @Query("SELECT * FROM device_manager WHERE parent_id=:id")
    public abstract OstDeviceManager[] getByParentId(String id);
}