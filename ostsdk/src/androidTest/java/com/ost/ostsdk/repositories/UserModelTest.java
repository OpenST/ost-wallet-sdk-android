package com.ost.ostsdk.repositories;

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.UserModel;
import com.ost.ostsdk.models.entities.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class UserModelTest {


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
        OstSdk.init(appContext.getApplicationContext());

        UserModel userModel = OstSdk.getUserModel();
        userModel.deleteAllUsers(null);
    }


    @Test
    public void testUserInsertion() throws JSONException, InterruptedException {
        // Context of the app under test.
        insertUserData();

        User user = OstSdk.getUserModel().getUserById("1");
        assertNotNull(user);
        assertEquals("user", user.getName());
        assertEquals("1", user.getId());
    }


    @Test
    public void testUserDeletion() throws JSONException, InterruptedException {
        // Context of the app under test.
        User user = insertUserData();

        UserModel userModel = OstSdk.getUserModel();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        userModel.deleteUser(user, new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        user = userModel.getUserById("1");
        assertNull(user);
    }

    @Test
    public void testUserInsertionInCache() throws JSONException, InterruptedException {
        // Context of the app under test.
        User user = insertUserData();

        OstSdkDatabase.getDatabase().userDao().delete(user);
        UserModel userModel = OstSdk.getUserModel();
        user = userModel.getUserById("1");
        assertNotNull(user);
        assertEquals("user", user.getName());
        assertEquals("1", user.getId());
    }

    @Test
    public void testUserInsertionInMemory() throws JSONException, InterruptedException {
        // Context of the app under test.
        User user = insertUserData();

        populateCache(2);

        OstSdkDatabase.getDatabase().userDao().delete(user);

        UserModel userModel = OstSdk.getUserModel();
        user = userModel.getUserById("1");
        assertNotNull(user);
        assertEquals("user", user.getName());
        assertEquals("1", user.getId());
    }

    private void populateCache(int cacheSizeToPopulate) throws JSONException, InterruptedException {

        for (int i = 0; i < cacheSizeToPopulate; i++) {
            insertUserData(i + 10);
        }
    }

    private User insertUserData() throws JSONException, InterruptedException {
        return insertUserData(1);
    }

    private User insertUserData(int param) throws JSONException, InterruptedException {
        JSONObject userObj = new JSONObject();

        userObj.put(User.ID, String.valueOf(param));
        userObj.put(User.ECONOMY_ID, "1");
        userObj.put(User.NAME, "user");
        userObj.put(User.TOKEN_HOLDER_ID, "1");

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        User user = OstSdk.getUserModel().initUser(userObj, new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        return user;
    }
}