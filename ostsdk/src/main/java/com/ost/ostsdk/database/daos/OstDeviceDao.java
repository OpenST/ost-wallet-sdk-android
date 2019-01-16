package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.OstBaseEntity;
import com.ost.ostsdk.models.entities.OstDevice;

@Dao
public abstract class OstDeviceDao implements OstBaseDao {

    public void insert(OstBaseEntity baseEntity) {
        this.insert((OstDevice) baseEntity);
    }

    public void insertAll(OstBaseEntity... baseEntity) {
        this.insertAll((OstDevice[]) baseEntity);
    }

    public void delete(OstBaseEntity baseEntity) {
        this.delete(baseEntity);
    }

    @Insert
    public abstract void insert(OstDevice ostDevice);

    @Insert
    public abstract void insertAll(OstDevice... ostDevice);

    @Query("DELETE FROM device WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM device WHERE id IN (:ids)")
    public abstract OstDevice[] getByIds(String[] ids);

    @Query("SELECT * FROM device WHERE id=:id")
    public abstract OstDevice getById(String id);

    @Query("DELETE FROM device")
    public abstract void deleteAll();

    @Query("SELECT * FROM device WHERE parent_id=:id")
    public abstract OstDevice[] getByParentId(String id);
}