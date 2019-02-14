package com.ost.mobilesdk.workflow;

import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstModelFactory;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.workflows.OstContextEntity;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;

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
        OstSdk.init(mAppContext);
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
            public void flowComplete(OstContextEntity ostContextEntity) {
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
}