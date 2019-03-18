/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.Impls.OstSecureKeyModelRepository;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spongycastle.crypto.generators.SCrypt;
import org.web3j.utils.Numeric;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class KeyGenProcessTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() throws ExecutionException, InterruptedException {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext, "");
        new OstSecureKeyModelRepository().deleteAllSecureKeys().get();
    }

    @Test
    public void testEncryptedKey() {
        // Context of the app under test.

        String walletAddress = new KeyGenProcess().execute("1");
        assertNotNull(walletAddress);
    }

    @Test
    public void testSCrypt() {
        // Context of the app under test.
        byte[] salt = "0x1234564835635432523acfdb1234564835635432523acfdb1234564835635432523acfdb".getBytes();
        long startTime = System.currentTimeMillis();
        byte[] byteOutput = SCrypt.generate("uPin".getBytes(), salt, (int) Math.pow(2, 16), 8, 1, 32);
        long deltaTime = System.currentTimeMillis() - startTime;
        String scryptOutput = Numeric.toHexString(byteOutput);
        System.out.println("Delta :" + deltaTime + "Scrypt out put " + scryptOutput);
    }
}
