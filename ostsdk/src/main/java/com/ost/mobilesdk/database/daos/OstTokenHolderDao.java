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
import com.ost.mobilesdk.models.entities.OstTokenHolder;

@Dao
public abstract class OstTokenHolderDao implements OstBaseDao {

    public void insert(OstBaseEntity baseEntity) {
        this.insert((OstTokenHolder) baseEntity);
    }

    public void insertAll(OstBaseEntity... baseEntity) {
        this.insertAll((OstTokenHolder[]) baseEntity);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(OstTokenHolder ostTokenHolder);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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