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
import com.ost.walletsdk.models.Impls.OstModelFactory;
import com.ost.walletsdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstDeviceRegisteredInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class OstRegisterDeviceTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() throws ExecutionException, InterruptedException {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext, "");
        new OstSecureKeyModelRepository().deleteAllSecureKeys().get();
    }

    @Test
    public void testRegisterDeviceVanillaFlow() {
        // Context of the app under test.
        String userId = "1";
        String tokenId = "58";
        final String[] address = new String[1];
        Looper.prepare();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        OstSdk.registerDevice(userId, tokenId, false, new AbsWorkFlowCallback() {
            @Override
            public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {
                try {
                    JSONObject jsonObject = apiParams.getJSONObject(OstSdk.DEVICE);
                    jsonObject.put(OstDevice.STATUS, OstDevice.CONST_STATUS.REGISTERED);
                    address[0] = jsonObject.getString(OstDevice.ADDRESS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ostDeviceRegisteredInterface.deviceRegistered(apiParams);
            }

            @Override
            public void flowComplete(OstWorkflowContext workflowContext, OstContextEntity ostContextEntity) {
                countDownLatch.countDown();
                OstDevice ostDevice = OstModelFactory.getDeviceModel().getEntityById(address[0]);
                Assert.assertEquals(OstDevice.CONST_STATUS.REGISTERED, ostDevice.getStatus());
            }
        });

        try {
            countDownLatch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Assert.fail();
        }
        Looper.myLooper().quit();
        Assert.assertFalse(false);
    }

    @Test
    public void testParamValidationFlow() {
        // Context of the app under test.
        String userId = "1";
        String tokenId = "58";
        Looper.prepare();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        OstSdk.registerDevice(userId, tokenId, false, new AbsWorkFlowCallback() {
            @Override
            public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Assert.fail();
        }
        Looper.myLooper().quit();
        Assert.assertFalse(false);
    }
}