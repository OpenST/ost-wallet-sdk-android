package com.ost.ostsdk.repositories;

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.models.Impls.UserModelRepository;
import com.ost.ostsdk.models.TaskCompleteCallback;
import com.ost.ostsdk.models.UserModel;
import com.ost.ostsdk.models.entities.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class UserModelTest {

    static UserModel mUserRepository;

    @ClassRule
    public static MigrationTestHelper testHelper =
            new MigrationTestHelper(
                    InstrumentationRegistry.getInstrumentation(),
                    OstSdkDatabase.class.getCanonicalName(),
                    new FrameworkSQLiteOpenHelperFactory());

    @BeforeClass
    public static void setUp() throws IOException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        testHelper.createDatabase("ostsdk_db", 1);
        OstSdkDatabase.initDatabase(appContext.getApplicationContext());

        mUserRepository = UserModelRepository.getInstance();
        mUserRepository.deleteAllUsers(null);
    }

    @Test
    public void testUserInsertion() throws JSONException {
        // Context of the app under test.
        JSONObject userObj = new JSONObject();

        userObj.put(User.ID, "1");
        userObj.put(User.ECONOMY_ID, "1");
        userObj.put(User.NAME, "user");
        userObj.put(User.TOKEN_HOLDER_ID, "1");

        User user = new User(userObj);
        mUserRepository.insertUser(user, new TaskCompleteCallback() {
            @Override
            public void onTaskComplete() {
                User user = mUserRepository.getUserById("1");
                assertEquals("user", user.getName());
                assertEquals("1", user.getId());
            }
        });
        System.out.print("j");
    }
}