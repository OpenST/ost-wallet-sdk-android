/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.ecKeyInteracts;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.mobilesdk.models.Impls.OstSessionKeyModelRepository;
import com.ost.mobilesdk.models.entities.OstSessionKey;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
public class OstKeyManagerTest {


    @BeforeClass
    public static void setUp() throws InterruptedException, ExecutionException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(appContext, "");

        CountDownLatch countDownLatch = new CountDownLatch(1);
        new OstSecureKeyModelRepository().deleteAllSecureKeys().get();
    }

    @Test
    public void testKeyMetaStruct() {
        String userId = "1";

//        OstKeyManager ostKeyManager = new OstKeyManager(userId);
//        OstKeyManager.KeyMetaStruct keyMetaStruct = new OstKeyManager.KeyMetaStruct("0xFd23B74B89a825E633AC2B51168674A2EC769f2b", "0xFd23B74B89a825E633AC2B51168674A2EC769f2b");
//        keyMetaStruct.addEthKeyIdentifier("0xFd23B74B89a825E633AC2B51168674A2EC769f2b", "iden");
//
//        byte[] bytes = ostKeyManager.createBytesFromObject(keyMetaStruct);
//        OstKeyManager.KeyMetaStruct keyMetaStruct1 = ostKeyManager.createObjectFromBytes(bytes);
//
//        Assert.assertEquals("0xFd23B74B89a825E633AC2B51168674A2EC769f2b", keyMetaStruct1.getApiAddress());
//        Assert.assertEquals("iden", keyMetaStruct1.getEthKeyIdentifier("0xFd23B74B89a825E633AC2B51168674A2EC769f2b"));
    }


    @Test
    public void testKeyGetDeviceAddress() {
        String userId = "1";

        OstKeyManager ostKeyManager = new OstKeyManager(userId);

        Assert.assertNotNull(ostKeyManager.getDeviceAddress());
    }

    @Test
    public void testCreateHDWallet() {
        String userId = "1";
        String seed = "123456789012345789012";
        OstKeyManager ostKeyManager1 = new OstKeyManager(userId);
//        String address1 = ostKeyManager1.createHDKeyAddress(seed.getBytes());
//
//        OstKeyManager ostKeyManager2 = new OstKeyManager(userId);
//        String address2 = ostKeyManager2.createHDKeyAddress(seed.getBytes());

//        Assert.assertEquals(address1, address2);
    }

    @Test
    public void testApiKey() {
        String userId = "1";

        OstKeyManager ostKeyManager = new OstKeyManager(userId);
        String apiKeyAddress1 = ostKeyManager.getApiKeyAddress();
        OstKeyManager ostKeyManager2 = new OstKeyManager(userId);

        Assert.assertEquals(apiKeyAddress1, ostKeyManager2.getApiKeyAddress());
    }

    @Test
    public void testCreateSessionKey() {
        String userId = "1";

        OstKeyManager ostKeyManager = new OstKeyManager(userId);
        String sessionKey = ostKeyManager.createSessionKey();
        OstSessionKey ostSessionKey = new OstSessionKeyModelRepository().getByKey(sessionKey);

        Assert.assertNotNull(ostSessionKey);
        Assert.assertNotNull(ostSessionKey.getData());
    }

}