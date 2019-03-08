package com.ost.mobilesdk.ecKeyInteracts;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.mobilesdk.ecKeyInteracts.impls.OstAndroidSecureStorage;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class OstSecureStorageTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() {
        mAppContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testDataConsistency() {
        // Context of the app under test.

        OstSecureStorage ostSecureStorage = OstAndroidSecureStorage.getInstance(mAppContext, "test");
        byte[] rawData = "Test".getBytes();
        byte[] encryptedData = ostSecureStorage.encrypt(rawData);
        byte[] decryptedRawData = ostSecureStorage.decrypt(encryptedData);
        String testString = new String(decryptedRawData);
        assertEquals("Test", testString);
    }
}
