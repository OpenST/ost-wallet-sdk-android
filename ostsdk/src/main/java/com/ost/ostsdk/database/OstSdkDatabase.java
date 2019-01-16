package com.ost.ostsdk.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ost.ostsdk.database.daos.ExecutableRuleDao;
import com.ost.ostsdk.database.daos.MultiSigDao;
import com.ost.ostsdk.database.daos.MultiSigOperationDao;
import com.ost.ostsdk.database.daos.MultiSigWalletDao;
import com.ost.ostsdk.database.daos.RuleDao;
import com.ost.ostsdk.database.daos.TokenDao;
import com.ost.ostsdk.database.daos.TokenHolderDao;
import com.ost.ostsdk.database.daos.TokenHolderSessionDao;
import com.ost.ostsdk.database.daos.UserDao;
import com.ost.ostsdk.models.entities.OstDevice;
import com.ost.ostsdk.models.entities.OstDeviceManager;
import com.ost.ostsdk.models.entities.OstDeviceOperation;
import com.ost.ostsdk.models.entities.OstExecutableRule;
import com.ost.ostsdk.models.entities.OstRule;
import com.ost.ostsdk.models.entities.OstToken;
import com.ost.ostsdk.models.entities.OstTokenHolder;
import com.ost.ostsdk.models.entities.OstTokenHolderSession;
import com.ost.ostsdk.models.entities.OstUser;

@Database(entities = {OstUser.class, OstRule.class, OstToken.class, OstExecutableRule.class,
        OstDeviceOperation.class, OstTokenHolder.class, OstTokenHolderSession.class,
        OstDevice.class, OstDeviceManager.class}, version = 1)
public abstract class OstSdkDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "ostsdk_db";

    public abstract UserDao userDao();

    public abstract RuleDao ruleDao();

    public abstract TokenDao tokenDao();

    public abstract ExecutableRuleDao executableRuleDao();

    public abstract MultiSigOperationDao multiSigOperationDao();

    public abstract TokenHolderDao tokenHolderDao();

    public abstract MultiSigWalletDao multiSigWalletDao();

    public abstract MultiSigDao multiSigDao();

    public abstract TokenHolderSessionDao tokenHolderSessionDao();

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