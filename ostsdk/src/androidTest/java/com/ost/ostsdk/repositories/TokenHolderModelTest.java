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
import com.ost.ostsdk.models.TokenHolderModel;
import com.ost.ostsdk.models.entities.TokenHolder;
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
public class TokenHolderModelTest {


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

        TokenHolderModel tokenHolderModel = ModelFactory.getTokenHolderModel();
        tokenHolderModel.deleteAllTokenHolders(new TaskCallback() {
        });
    }


    @Test
    public void testTokenHolderInsertion() throws JSONException, InterruptedException {
        // Context of the app under test.
        insertTokenHolderData();

        TokenHolder tokenHolder = OstSdk.getUser("1").getTokenHolder("1");
        assertNotNull(tokenHolder);
        assertEquals(1, tokenHolder.getRequirements());
        assertEquals("address", tokenHolder.getAddress());
    }


    @Test
    public void testTokenHolderDeletion() throws JSONException, InterruptedException {
        // Context of the app under test.
        TokenHolder tokenHolder = insertTokenHolderData();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        OstSdk.getUser("1").delTokenHolder(tokenHolder.getId(), new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        tokenHolder = OstSdk.getUser("1").getTokenHolder("1");
        assertNull(tokenHolder);
    }

    @Test
    public void testUserInsertionInCache() throws JSONException, InterruptedException {
        // Context of the app under test.
        TokenHolder tokenHolder = insertTokenHolderData();

        OstSdkDatabase.getDatabase().tokenHolderDao().delete(tokenHolder.getId());
        tokenHolder = OstSdk.getUser("1").getTokenHolder("1");
        assertNotNull(tokenHolder);
        assertEquals(1, tokenHolder.getRequirements());
        assertEquals("address", tokenHolder.getAddress());
    }

    private void populateCache(int cacheSizeToPopulate) throws JSONException, InterruptedException {

        for (int i = 0; i < cacheSizeToPopulate; i++) {
            insertTokenHolderData(i + 10);
        }
    }

    private TokenHolder insertTokenHolderData() throws JSONException, InterruptedException {
        return insertTokenHolderData(1);
    }

    private TokenHolder insertTokenHolderData(int param) throws JSONException, InterruptedException {
        JSONObject userObj = new JSONObject();

        userObj.put(User.ID, String.valueOf(param));
        userObj.put(User.ECONOMY_ID, "1");
        userObj.put(User.NAME, "user");
        userObj.put(User.TOKEN_HOLDER_ID, "1");
        userObj.put(User.MULTI_SIG_ID, "1");


        final CountDownLatch countDownLatch = new CountDownLatch(1);

        User user = OstSdk.initUser(userObj, new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TokenHolder.ID, "1");
        jsonObject.put(TokenHolder.USER_ID, "1");
        jsonObject.put(TokenHolder.EXECUTE_RULE_CALL_PREFIX, "tokenHolderNo1");
        jsonObject.put(TokenHolder.REQUIREMENTS, 1);
        jsonObject.put(TokenHolder.ADDRESS, "address");

        return user.initTokenHolder(jsonObject);
    }
}