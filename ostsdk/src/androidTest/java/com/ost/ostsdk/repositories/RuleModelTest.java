package com.ost.ostsdk.repositories;

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.models.Impls.ModelFactory;
import com.ost.ostsdk.models.RuleModel;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.Rule;
import com.ost.ostsdk.models.entities.Token;
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
public class RuleModelTest {


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

        RuleModel ruleModel = ModelFactory.getRuleModel();
        ruleModel.deleteAllRules(new TaskCallback() {
        });
    }


    @Test
    public void testRuleInsertion() throws JSONException, InterruptedException {
        // Context of the app under test.
        insertRuleData();

        Rule rule = OstSdk.getToken("1").getRule("1");
        assertNotNull(rule);
        assertEquals("ruleNo1", rule.getName());
        assertEquals("1", rule.getId());
    }


    @Test
    public void testRuleDeletion() throws JSONException, InterruptedException {
        // Context of the app under test.
        Rule rule = insertRuleData();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        OstSdk.getToken("1").delRule(rule.getId(), new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        rule = OstSdk.getToken("1").getRule("1");
        assertNull(rule);
    }

    @Test
    public void testUserInsertionInCache() throws JSONException, InterruptedException {
        // Context of the app under test.
        Rule rule = insertRuleData();

        OstSdkDatabase.getDatabase().ruleDao().delete(rule.getId());
        rule = OstSdk.getToken("1").getRule("1");
        assertNotNull(rule);
        assertEquals("ruleNo1", rule.getName());
        assertEquals("1", rule.getId());
    }

    private void populateCache(int cacheSizeToPopulate) throws JSONException, InterruptedException {

        for (int i = 0; i < cacheSizeToPopulate; i++) {
            insertRuleData(i + 10);
        }
    }

    private Rule insertRuleData() throws JSONException, InterruptedException {
        return insertRuleData(1);
    }

    private Rule insertRuleData(int param) throws JSONException, InterruptedException {
        JSONObject tokenJson = new JSONObject();

        tokenJson.put(User.ID, String.valueOf(param));


        final CountDownLatch countDownLatch = new CountDownLatch(1);

        Token token = OstSdk.registerToken(tokenJson, new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Rule.ID, "1");
        jsonObject.put(Rule.TOKEN_ID, "1");
        jsonObject.put(Rule.ABI, "asdfgh");
        jsonObject.put(Rule.NAME, "ruleNo1");
        jsonObject.put(Rule.ADDRESS, "address");

        return token.initRule(jsonObject);
    }
}