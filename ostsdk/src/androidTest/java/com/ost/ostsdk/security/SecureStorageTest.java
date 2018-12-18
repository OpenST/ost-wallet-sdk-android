package com.ost.ostsdk.security;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.ostsdk.security.impls.AndroidSecureStorage;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SecureStorageTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() {
        mAppContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testDataConsistency() {
        // Context of the app under test.

        SecureStorage secureStorage = AndroidSecureStorage.getInstance(mAppContext, "test");
        byte[] rawData = "Test".getBytes();
        byte[] encryptedData = secureStorage.encrypt(rawData);
        byte[] decryptedRawData = secureStorage.decrypt(encryptedData);
        String testString = new String(decryptedRawData);
        assertEquals("Test", testString);
    }
}
