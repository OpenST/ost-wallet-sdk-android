package com.ost.mobilesdk.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ost.mobilesdk.database.daos.OstSecureKeyDao;
import com.ost.mobilesdk.models.entities.OstSecureKey;

@Database(entities = {OstSecureKey.class}, version = 1)
public abstract class OstSdkKeyDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "ostsdkkey_db";

    public abstract OstSecureKeyDao secureKeyDao();

    private static volatile OstSdkKeyDatabase INSTANCE;

    public static OstSdkKeyDatabase initDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (OstSdkKeyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            OstSdkKeyDatabase.class, DATABASE_NAME)
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