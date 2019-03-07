package com.ost.mobilesdk.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;

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
        byte[] byteOutput = SCrypt.generate("uPin".getBytes(), salt, (int) Math.pow(2, 14), 8, 1, 32);
        long deltaTime = System.currentTimeMillis() - startTime;
        String scryptOutput = Numeric.toHexString(byteOutput);
        System.out.println("Delta :" + deltaTime + "Scrypt out put " + scryptOutput);
    }
}
