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

import com.ost.walletsdk.models.entities.OstSecureKey;

@Dao
public interface OstSecureKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OstSecureKey ostSecureKey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(OstSecureKey... ostSecureKey);

    @Query("DELETE FROM bytes_storage WHERE `id`=:id")
    void delete(String id);

    @Query("SELECT * FROM bytes_storage WHERE `id` IN (:id)")
    OstSecureKey[] getByIds(String[] id);

    @Query("SELECT * FROM bytes_storage WHERE `id`=:id")
    OstSecureKey getById(String id);

    @Query("DELETE FROM bytes_storage")
    void deleteAll();
}