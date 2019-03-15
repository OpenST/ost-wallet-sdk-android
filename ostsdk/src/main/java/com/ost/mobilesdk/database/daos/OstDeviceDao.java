/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstDevice;

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(OstDevice ostDevice);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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