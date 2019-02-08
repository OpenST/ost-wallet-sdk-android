package com.ost.mobilesdk.security;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.mobilesdk.models.Impls.OstSessionKeyModelRepository;
import com.ost.mobilesdk.models.OstTaskCallback;
import com.ost.mobilesdk.models.entities.OstSessionKey;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class OstKeyManagerTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() throws InterruptedException {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        new OstSecureKeyModelRepository().deleteAllSecureKeys(new OstTaskCallback() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(10, TimeUnit.SECONDS);
    }


    @Test
    public void testKeyMetaStruct() {
        String userId = "1";

        OstKeyManager ostKeyManager = new OstKeyManager(userId);
        OstKeyManager.KeyMetaStruct keyMetaStruct = new OstKeyManager.KeyMetaStruct("abc");
        keyMetaStruct.addEthKeyIdentifier("abc", "iden");

        byte[] bytes = ostKeyManager.createBytesFromObject(keyMetaStruct);
        OstKeyManager.KeyMetaStruct keyMetaStruct1 = ostKeyManager.createObjectFromBytes(bytes);

        Assert.assertEquals("abc", keyMetaStruct1.getApiAddress());
        Assert.assertEquals("iden", keyMetaStruct1.getEthKeyIdentifier("abc"));
    }

    @Test
    public void testKeyCreation() {
        String userId = "1";

        OstKeyManager ostKeyManager = new OstKeyManager(userId);
        String address = ostKeyManager.createKey();

        Assert.assertTrue(ostKeyManager.hasAddress(address));
    }

    @Test
    public void testKeyCreationWithMnemonics() {
        String userId = "1";

        OstKeyManager ostKeyManager = new OstKeyManager(userId);
        String address = ostKeyManager.createKeyWithMnemonic();

        Assert.assertTrue(ostKeyManager.hasAddress(address));

        String[] mnemonics = ostKeyManager.getMnemonics(address);

        Assert.assertNotNull(mnemonics);
    }

    @Test
    public void testCreateHDWallet() {
        String userId = "1";
        String seed = "123456789012345789012";
        OstKeyManager ostKeyManager1 = new OstKeyManager(userId);
        String address1 = ostKeyManager1.createHDKey(seed.getBytes());

        OstKeyManager ostKeyManager2 = new OstKeyManager(userId);
        String address2 = ostKeyManager2.createHDKey(seed.getBytes());

        Assert.assertEquals(address1, address2);
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