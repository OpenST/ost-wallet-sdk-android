/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ost.walletsdk.database.daos.OstSecureKeyDao;
import com.ost.walletsdk.models.entities.OstSecureKey;

@Database(entities = {OstSecureKey.class}, version = 1)
public abstract class OstSdkKeyDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "ostsdkkey_db";

    public abstract OstSecureKeyDao secureKeyDao();

    private static volatile OstSdkKeyDatabase INSTANCE;

    public static OstSdkKeyDatabase initDatabase(final Context context) {
        String databasePath = String.format("%s/%s", context.getNoBackupFilesDir(),DATABASE_NAME);
        if (INSTANCE == null) {
            synchronized (OstSdkKeyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            OstSdkKeyDatabase.class, databasePath)
                            .allowMainThreadQueries()
                            .addMigrations(
                                    /*Add your migration class object here
                                     * eg: new Migration_1_2(1,2)
                                     * */
                            )
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static OstSdkKeyDatabase getDatabase() {
        if (INSTANCE == null) {
            throw new RuntimeException("OstSdkDatabase not initialized");
        }
        return INSTANCE;
    }
}