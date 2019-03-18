/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.models.entities.OstDeviceManagerOperation;

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