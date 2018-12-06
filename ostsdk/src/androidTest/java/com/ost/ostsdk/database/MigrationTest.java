package com.ost.ostsdk.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.ostsdk.database.migrations.MigrationSample;
import com.ost.ostsdk.models.UserModel;

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

    static UserModel mUserRepository;

    @BeforeClass
    public static void setUp() {
        Context appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testTableMigration() throws IOException {
        SupportSQLiteDatabase db =testHelper.createDatabase("ostsdk_db",1);
        ContentValues cv = new ContentValues();
        cv.put("economy_id",123);
        cv.put("token_holder_id",123);
        cv.put("name","name");
        cv.put("uts",123);
        db.insert("user",SQLiteDatabase.CONFLICT_REPLACE, cv);
        testHelper.closeWhenFinished(db);

        db = testHelper.runMigrationsAndValidate("ostsdk_db", 1, true, new MigrationSample(1, 1));

    }
}
