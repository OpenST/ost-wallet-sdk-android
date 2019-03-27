/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.workflow;

import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class OstPerformTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() throws ExecutionException, InterruptedException {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.initialize(mAppContext, "");
        new OstSecureKeyModelRepository().deleteAllSecureKeys().get();
    }

    @Test
    public void testAddDeviceWithQR() throws JSONException {
        // Context of the app under test.
        String userId = "1234-5678-9101-abcd";
        JSONObject jsonObject = new JSONObject("{\"dd\":\"AUTHORIZE_DEVICE\",\"ddv\":\"1.0\",\"d\":{\"da\":\"0x45d86614d067779eA41a28a8A1Ba09Da45ccBdF5\"}}");
        String data = jsonObject.toString();
        Looper.prepare();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        OstSdk.performQRAction(userId, data, new AbsWorkFlowCallback() {
            @Override
            public void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {
                Assert.assertEquals(ostWorkflowContext.getWorkflow_type(), OstWorkflowContext.WORKFLOW_TYPE.PERFORM_QR_ACTION);
                Assert.assertTrue(ostContextEntity.getEntityType().equalsIgnoreCase(OstSdk.DEVICE));

                countDownLatch.countDown();
                ostVerifyDataInterface.cancelFlow();
            }

            @Override
            public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
                super.flowInterrupt(ostWorkflowContext, ostError);
                Assert.assertTrue(true);
            }
        });

        try {
            countDownLatch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Assert.fail();
        }
        Looper.myLooper().quit();
        if (countDownLatch.getCount() == 1) {
            Assert.fail();
        } else {
            Assert.assertTrue(true);
        }
    }
}