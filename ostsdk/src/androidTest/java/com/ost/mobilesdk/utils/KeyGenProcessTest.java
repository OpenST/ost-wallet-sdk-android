package com.ost.mobilesdk.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class KeyGenProcessTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() throws ExecutionException, InterruptedException {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext);
        new OstSecureKeyModelRepository().deleteAllSecureKeys().get();
    }

    @Test
    public void testEncryptedKey() {
        // Context of the app under test.

        String walletAddress = new KeyGenProcess().execute("1");
        assertNotNull(walletAddress);
    }
}
