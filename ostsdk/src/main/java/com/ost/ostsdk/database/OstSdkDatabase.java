package com.ost.ostsdk.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ost.ostsdk.database.daos.OstDeviceOperationDao;
import com.ost.ostsdk.database.daos.OstExecutableRuleDao;
import com.ost.ostsdk.database.daos.OstDeviceManagerDao;
import com.ost.ostsdk.database.daos.OstDeviceDao;
import com.ost.ostsdk.database.daos.OstRuleDao;
import com.ost.ostsdk.database.daos.OstTokenDao;
import com.ost.ostsdk.database.daos.OstTokenHolderDao;
import com.ost.ostsdk.database.daos.OstSessionDao;
import com.ost.ostsdk.database.daos.OstUserDao;
import com.ost.ostsdk.models.entities.OstDevice;
import com.ost.ostsdk.models.entities.OstDeviceManager;
import com.ost.ostsdk.models.entities.OstDeviceOperation;
import com.ost.ostsdk.models.entities.OstExecutableRule;
import com.ost.ostsdk.models.entities.OstRule;
import com.ost.ostsdk.models.entities.OstSession;
import com.ost.ostsdk.models.entities.OstToken;
import com.ost.ostsdk.models.entities.OstTokenHolder;
import com.ost.ostsdk.models.entities.OstUser;

@Database(entities = {OstUser.class, OstRule.class, OstToken.class, OstExecutableRule.class,
        OstDeviceOperation.class, OstTokenHolder.class, OstSession.class,
        OstDevice.class, OstDeviceManager.class}, version = 1)
public abstract class OstSdkDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "ostsdk_db";

    public abstract OstUserDao userDao();

    public abstract OstRuleDao ruleDao();

    public abstract OstTokenDao tokenDao();

    public abstract OstExecutableRuleDao executableRuleDao();

    public abstract OstDeviceOperationDao multiSigOperationDao();

    public abstract OstTokenHolderDao tokenHolderDao();

    public abstract OstDeviceDao multiSigWalletDao();

    public abstract OstDeviceManagerDao multiSigDao();

    public abstract OstSessionDao tokenHolderSessionDao();

    private static volatile OstSdkDatabase INSTANCE;

    public static OstSdkDatabase initDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (OstSdkDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            OstSdkDatabase.class, DATABASE_NAME)
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