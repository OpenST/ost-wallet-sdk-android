package com.ost.mobilesdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;

@Dao
public abstract class OstDeviceOperationDao implements OstBaseDao {

    public void insert(OstBaseEntity baseEntity) {
        this.insert((OstDeviceManagerOperation) baseEntity);
    }

    public void insertAll(OstBaseEntity... baseEntity) {
        this.insertAll((OstDeviceManagerOperation[]) baseEntity);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(OstDeviceManagerOperation ostDeviceManagerOperation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(OstDeviceManagerOperation... ostDeviceManagerOperation);

    @Query("DELETE FROM device_manager WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM device_manager WHERE id IN (:ids)")
    public abstract OstDeviceManagerOperation[] getByIds(String[] ids);

    @Query("SELECT * FROM device_manager WHERE id=:id")
    public abstract OstDeviceManagerOperation getById(String id);

    @Query("DELETE FROM device_manager")
    public abstract void deleteAll();

    @Query("SELECT * FROM device_manager WHERE parent_id=:id")
    public abstract OstDeviceManagerOperation[] getByParentId(String id);
}