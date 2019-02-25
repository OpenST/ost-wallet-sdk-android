package com.ost.mobilesdk.repositories;

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.models.Impls.OstModelFactory;
import com.ost.mobilesdk.models.OstTokenHolderModel;
import com.ost.mobilesdk.models.entities.OstTokenHolder;
import com.ost.mobilesdk.models.entities.OstUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class OstTokenHolderModelTest {


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
        OstSdk.init(appContext.getApplicationContext(), "");

        OstTokenHolderModel tokenHolderModel = OstModelFactory.getTokenHolderModel();
        tokenHolderModel.deleteAllEntities();
    }


    @Test
    public void testTokenHolderInsertion() throws JSONException, InterruptedException {
        // Context of the app under test.
        insertTokenHolderData();

        OstTokenHolder ostTokenHolder = OstSdk.getUser("1").getTokenHolder();
        assertNotNull(ostTokenHolder);
        assertEquals("address", ostTokenHolder.getAddress());
    }


    @Test
    public void testTokenHolderDeletion() throws JSONException, InterruptedException {
        // Context of the app under test.
        OstTokenHolder ostTokenHolder = insertTokenHolderData();

//        final CountDownLatch countDownLatch = new CountDownLatch(1);
        OstSdk.getUser("1").delTokenHolder(ostTokenHolder.getId());

//        countDownLatch.await(5, TimeUnit.SECONDS);

        ostTokenHolder = OstSdk.getUser("1").getTokenHolder();
        assertNull(ostTokenHolder);
    }

    @Test
    public void testUserInsertionInCache() throws JSONException, InterruptedException {
        // Context of the app under test.
        OstTokenHolder ostTokenHolder = insertTokenHolderData();

        OstSdkDatabase.getDatabase().tokenHolderDao().delete(ostTokenHolder.getId());
        ostTokenHolder = OstSdk.getUser("1").getTokenHolder();
        assertNotNull(ostTokenHolder);
        assertEquals("address", ostTokenHolder.getAddress());
    }

    private void populateCache(int cacheSizeToPopulate) throws JSONException, InterruptedException {

        for (int i = 0; i < cacheSizeToPopulate; i++) {
            insertTokenHolderData(i + 10);
        }
    }

    private OstTokenHolder insertTokenHolderData() throws JSONException, InterruptedException {
        return insertTokenHolderData(1);
    }

    private OstTokenHolder insertTokenHolderData(int param) throws JSONException {
        JSONObject userObj = new JSONObject();

        userObj.put(OstUser.ID, String.valueOf(param));
        userObj.put(OstUser.TOKEN_ID, "1");
        userObj.put(OstUser.TYPE,OstUser.TYPE_VALUE.USER);
        userObj.put(OstUser.TOKEN_HOLDER_ADDRESS, "1");
        userObj.put(OstUser.DEVICE_MANAGER_ADDRESS, "1");


//        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OstUser ostUser = OstUser.parse(userObj);

//        countDownLatch.await(5, TimeUnit.SECONDS);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(OstTokenHolder.ID, "1");
//        jsonObject.put(OstTokenHolder.EXECUTE_RULE_CALL_PREFIX, "tokenHolderNo1");
//        jsonObject.put(OstTokenHolder.REQUIREMENTS, 1);
        jsonObject.put(OstTokenHolder.ADDRESS, "address");

        return OstTokenHolder.parse(jsonObject);
    }
}