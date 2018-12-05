package com.ost.ostsdk.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ost.ostsdk.data.database.daos.RuleDao;
import com.ost.ostsdk.data.database.daos.UserDao;
import com.ost.ostsdk.data.models.entities.RuleEntity;
import com.ost.ostsdk.data.models.entities.UserEntity;

@Database(entities = {UserEntity.class, RuleEntity.class}, version = 1)
public abstract class OstSdkDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "ostsdk_db";

    public abstract UserDao userDao();

    public abstract RuleDao ruleDao();

    private static volatile OstSdkDatabase INSTANCE;

    public static OstSdkDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (OstSdkDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            OstSdkDatabase.class, DATABASE_NAME)
                            .addMigrations()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}