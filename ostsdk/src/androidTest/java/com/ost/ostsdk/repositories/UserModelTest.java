package com.ost.ostsdk.repositories;

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.models.Impls.ModelFactory;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.UserModel;
import com.ost.ostsdk.models.entities.OstUser;

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

        UserModel userModel = ModelFactory.getUserModel();
        userModel.deleteAllUsers(new TaskCallback() {
        });
    }


    @Test
    public void testUserInsertion() throws JSONException, InterruptedException {
        // Context of the app under test.
        insertUserData();

        OstUser ostUser = OstSdk.getUser("1");
        assertNotNull(ostUser);
        assertEquals("ostUser", ostUser.getName());
        assertEquals("1", ostUser.getId());
    }


    @Test
    public void testUserDeletion() throws JSONException, InterruptedException {
        // Context of the app under test.
        OstUser ostUser = insertUserData();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        OstSdk.delUser(ostUser.getId(), new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        ostUser = OstSdk.getUser("1");
        assertNull(ostUser);
    }

    @Test
    public void testUserInsertionInCache() throws JSONException, InterruptedException {
        // Context of the app under test.
        OstUser ostUser = insertUserData();

        OstSdkDatabase.getDatabase().userDao().delete(ostUser.getId());
        ostUser = OstSdk.getUser("1");
        assertNotNull(ostUser);
        assertEquals("ostUser", ostUser.getName());
        assertEquals("1", ostUser.getId());
    }

    @Test
    public void testUserInsertionInMemory() throws JSONException, InterruptedException {
        // Context of the app under test.
        OstUser ostUser = insertUserData();

        populateCache(2);

        OstSdkDatabase.getDatabase().userDao().delete(ostUser.getId());

        ostUser = OstSdk.getUser("1");
        assertNotNull(ostUser);
        assertEquals("ostUser", ostUser.getName());
        assertEquals("1", ostUser.getId());
    }

    private void populateCache(int cacheSizeToPopulate) throws JSONException, InterruptedException {

        for (int i = 0; i < cacheSizeToPopulate; i++) {
            insertUserData(i + 10);
        }
    }

    private OstUser insertUserData() throws JSONException, InterruptedException {
        return insertUserData(1);
    }

    private OstUser insertUserData(int param) throws JSONException, InterruptedException {
        JSONObject userObj = new JSONObject();

        userObj.put(OstUser.ID, String.valueOf(param));
        userObj.put(OstUser.TOKEN_ID, "1");
        userObj.put(OstUser.NAME, "ostUser");
        userObj.put(OstUser.TOKEN_HOLDER_ID, "1");
        userObj.put(OstUser.MULTI_SIG_ID, "1");

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OstUser ostUser = OstSdk.initUser(userObj, new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        return ostUser;
    }
}