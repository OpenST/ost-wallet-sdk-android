package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.OstBaseEntity;
import com.ost.ostsdk.models.entities.OstDeviceOperation;

@Dao
public abstract class MultiSigOperationDao implements BaseDao {

    public void insert(OstBaseEntity baseEntity) {
        this.insert((OstDeviceOperation) baseEntity);
    }

    public void insertAll(OstBaseEntity... baseEntity) {
        this.insertAll((OstDeviceOperation[]) baseEntity);
    }

    @Insert
    public abstract void insert(OstDeviceOperation ostDeviceOperation);

    @Insert
    public abstract void insertAll(OstDeviceOperation... ostDeviceOperation);

    @Query("DELETE FROM device_operation WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM device_operation WHERE id IN (:ids)")
    public abstract OstDeviceOperation[] getByIds(String[] ids);

    @Query("SELECT * FROM device_operation WHERE id=:id")
    public abstract OstDeviceOperation getById(String id);

    @Query("DELETE FROM device_operation")
    public abstract void deleteAll();

    @Query("SELECT * FROM device_operation WHERE parent_id=:id")
    public abstract OstDeviceOperation[] getByParentId(String id);
}