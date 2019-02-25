package com.ost.mobilesdk.workflow;

import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.workflows.OstContextEntity;
import com.ost.mobilesdk.workflows.OstWorkflowContext;
import com.ost.mobilesdk.workflows.errors.OstError;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class OstActivateUserTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() throws ExecutionException, InterruptedException {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext, "");
        new OstSecureKeyModelRepository().deleteAllSecureKeys().get();
    }

    @Test
    public void testActivateUserInvalidParams() {
        String uPin = "123456";
        String password = "password";
        String userId = "1";

        long expirationHeight = System.currentTimeMillis();
        String spendingLimit = "100000";
        Looper.prepare();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        OstSdk.activateUser("", uPin, password , expirationHeight, spendingLimit, new AbsWorkFlowCallback() {
            @Override
            public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
                super.flowComplete(ostWorkflowContext, ostContextEntity);
            }

            @Override
            public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
                super.flowInterrupt(ostWorkflowContext, ostError);
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await(20, TimeUnit.SECONDS);
            Assert.assertTrue(true);
        } catch (InterruptedException e) {
            Assert.fail();
        }
        Looper.myLooper().quit();

    }

    @Test
    public void testActivateUserDeviceUnRegisted() {
        String uPin = "123456";
        String password = "password";
        String userId = "qweqw-2132-sdfsdf-323";

        long expirationHeight = System.currentTimeMillis();
        String spendingLimit = "100000";
        Looper.prepare();

        OstUser ostUser = OstUser.initUser(userId, userId);

        OstKeyManager ostKeyManager = new OstKeyManager(userId);
        ostKeyManager.getApiKeyAddress();
        OstDevice ostDevice = OstDevice.init(ostKeyManager.getDeviceAddress(), ostKeyManager.getApiKeyAddress(), userId);
        try {
            Field field = ostUser.getClass().getDeclaredField("currentDevice");
            field.setAccessible(true);
            field.set(ostUser, ostDevice);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        CountDownLatch countDownLatch = new CountDownLatch(1);
        OstSdk.activateUser(userId, uPin, password , expirationHeight, spendingLimit, new AbsWorkFlowCallback() {
            @Override
            public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
                super.flowComplete(ostWorkflowContext, ostContextEntity);
            }

            @Override
            public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
                super.flowInterrupt(ostWorkflowContext, ostError);
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await(20, TimeUnit.SECONDS);
            Assert.assertTrue(true);
        } catch (InterruptedException e) {
            Assert.fail();
        }
        Looper.myLooper().quit();
    }
}