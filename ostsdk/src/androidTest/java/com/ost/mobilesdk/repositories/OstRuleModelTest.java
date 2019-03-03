package com.ost.mobilesdk.repositories;

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.models.Impls.OstModelFactory;
import com.ost.mobilesdk.models.OstRuleModel;
import com.ost.mobilesdk.models.entities.OstRule;
import com.ost.mobilesdk.models.entities.OstToken;

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
public class OstRuleModelTest {


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

        OstRuleModel ostRuleModel = OstModelFactory.getRuleModel();
        ostRuleModel.deleteAllEntities();
    }


    @Test
    public void testRuleInsertion() throws JSONException, InterruptedException {
        // Context of the app under test.
        insertRuleData();

        OstRule ostRule = OstSdk.getToken("1").getRule("1");
        assertNotNull(ostRule);
        assertEquals("ruleNo1", ostRule.getName());
        assertEquals("1", ostRule.getId());
    }


    @Test
    public void testRuleDeletion() throws JSONException, InterruptedException {
        // Context of the app under test.
        OstRule ostRule = insertRuleData();

//        final CountDownLatch countDownLatch = new CountDownLatch(1);
        OstSdk.getToken("1").delRule(ostRule.getId());

//        countDownLatch.await(5, TimeUnit.SECONDS);

        ostRule = OstSdk.getToken("1").getRule("1");
        assertNull(ostRule);
    }

    @Test
    public void testUserInsertionInCache() throws JSONException, InterruptedException {
        // Context of the app under test.
        OstRule ostRule = insertRuleData();

        OstSdkDatabase.getDatabase().ruleDao().delete(ostRule.getId());
        ostRule = OstSdk.getToken("1").getRule("1");
        assertNotNull(ostRule);
        assertEquals("ruleNo1", ostRule.getName());
        assertEquals("1", ostRule.getId());
    }

    private void populateCache(int cacheSizeToPopulate) throws JSONException {

        for (int i = 0; i < cacheSizeToPopulate; i++) {
            insertRuleData(i + 10);
        }
    }

    private OstRule insertRuleData() throws JSONException {
        return insertRuleData(1);
    }

    private OstRule insertRuleData(int param) throws JSONException {

//        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OstToken ostToken = OstToken.init(String.valueOf(param));

//        countDownLatch.await(5, TimeUnit.SECONDS);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(OstRule.ID, "1");
        jsonObject.put(OstRule.TOKEN_ID, "1");
        jsonObject.put(OstRule.ABI, "asdfgh");
        jsonObject.put(OstRule.NAME, "ruleNo1");
        jsonObject.put(OstRule.ADDRESS, "address");

        return ostToken.initRule(jsonObject);
    }
}