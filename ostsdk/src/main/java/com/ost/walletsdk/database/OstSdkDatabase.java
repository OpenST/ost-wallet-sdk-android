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
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.ost.walletsdk.database.daos.OstDeviceDao;
import com.ost.walletsdk.database.daos.OstDeviceManagerDao;
import com.ost.walletsdk.database.daos.OstDeviceOperationDao;
import com.ost.walletsdk.database.daos.OstRuleDao;
import com.ost.walletsdk.database.daos.OstSessionDao;
import com.ost.walletsdk.database.daos.OstSessionKeyDao;
import com.ost.walletsdk.database.daos.OstTokenDao;
import com.ost.walletsdk.database.daos.OstTokenHolderDao;
import com.ost.walletsdk.database.daos.OstTransactionDao;
import com.ost.walletsdk.database.daos.OstUserDao;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstDeviceManager;
import com.ost.walletsdk.models.entities.OstDeviceManagerOperation;
import com.ost.walletsdk.models.entities.OstRule;
import com.ost.walletsdk.models.entities.OstSession;
import com.ost.walletsdk.models.entities.OstSessionKey;
import com.ost.walletsdk.models.entities.OstToken;
import com.ost.walletsdk.models.entities.OstTokenHolder;
import com.ost.walletsdk.models.entities.OstTransaction;
import com.ost.walletsdk.models.entities.OstUser;

@Database(entities = {OstUser.class, OstRule.class, OstToken.class, OstTransaction.class,
        OstDeviceManagerOperation.class, OstTokenHolder.class, OstSession.class,
        OstDevice.class, OstDeviceManager.class, OstSessionKey.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class OstSdkDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "ostsdk_db";

    public abstract OstUserDao userDao();

    public abstract OstRuleDao ruleDao();

    public abstract OstTokenDao tokenDao();

    public abstract OstTransactionDao executableRuleDao();

    public abstract OstDeviceOperationDao multiSigOperationDao();

    public abstract OstTokenHolderDao tokenHolderDao();

    public abstract OstDeviceDao multiSigWalletDao();

    public abstract OstDeviceManagerDao multiSigDao();

    public abstract OstSessionDao tokenHolderSessionDao();

    public abstract OstSessionKeyDao sessionKeyDao();


    private static volatile OstSdkDatabase INSTANCE;

    public static OstSdkDatabase initDatabase(final Context context) {
        String databasePath = String.format("%s/%s", context.getNoBackupFilesDir(),DATABASE_NAME);
        if (INSTANCE == null) {
            synchronized (OstSdkDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            OstSdkDatabase.class, databasePath)
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

    public static OstSdkDatabase getDatabase() {
        if (INSTANCE == null) {
            throw new RuntimeException("OstSdkDatabase not initialized");
        }
        return INSTANCE;
    }
}