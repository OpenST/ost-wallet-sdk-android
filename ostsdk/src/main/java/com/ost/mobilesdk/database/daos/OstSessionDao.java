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
import com.ost.mobilesdk.models.entities.OstSession;

@Dao
public abstract class OstSessionDao implements OstBaseDao {

    public void insert(OstBaseEntity baseEntity) {
        this.insert((OstSession) baseEntity);
    }

    public void insertAll(OstBaseEntity... baseEntity) {
        this.insertAll((OstSession[]) baseEntity);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(OstSession ostSession);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(OstSession... ostSession);

    @Query("DELETE FROM session WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM session WHERE id IN (:ids)")
    public abstract OstSession[] getByIds(String[] ids);

    @Query("SELECT * FROM session WHERE id=:id")
    public abstract OstSession getById(String id);

    @Query("DELETE FROM session")
    public abstract void deleteAll();

    @Query("SELECT * FROM session WHERE parent_id=:id")
    public abstract OstSession[] getByParentId(String id);
}
