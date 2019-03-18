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

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.walletsdk.models.OstUserModel;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class MigrationTest {

    @Rule
    public MigrationTestHelper testHelper =
            new MigrationTestHelper(
                    InstrumentationRegistry.getInstrumentation(),
                    OstSdkDatabase.class.getCanonicalName(),
                    new FrameworkSQLiteOpenHelperFactory());

    static OstUserModel mOstUserRepository;

    @BeforeClass
    public static void setUp() {
        Context appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testTableMigration() throws IOException {
//        SupportSQLiteDatabase db = testHelper.createDatabase("ostsdk_db",1);
//        ContentValues cv = new ContentValues();
//        cv.put("economy_id","123");
//        cv.put("token_holder_id","123");
//        cv.put("name","name");
//        cv.put("uts",123);
//        db.insertOrUpdate("user",SQLiteDatabase.CONFLICT_REPLACE, cv);
//        testHelper.closeWhenFinished(db);
//
//        db = testHelper.runMigrationsAndValidate("ostsdk_db", 1, true, new MigrationSample(1, 1));

    }
}
