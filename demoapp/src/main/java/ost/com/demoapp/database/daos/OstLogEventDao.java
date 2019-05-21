/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import ost.com.demoapp.entity.OstLogEvent;

@Dao
public interface OstLogEventDao {
    @Insert
    void insert(OstLogEvent ostLogEvent);

    @Query("SELECT * FROM log_events WHERE id > :timestamp")
    OstLogEvent[] getLogsAfter(long timestamp);

    @Query("DELETE FROM log_events")
    void deleteAll();

    @Query("SELECT * FROM log_events order by id desc LIMIT :noOfEvents")
    OstLogEvent[] getLogs(int noOfEvents);
}