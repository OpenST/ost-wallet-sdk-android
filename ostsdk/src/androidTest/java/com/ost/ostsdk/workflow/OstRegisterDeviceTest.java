package com.ost.ostsdk.workflow;

import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.OstModelFactory;
import com.ost.ostsdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.entities.OstDevice;
import com.ost.ostsdk.workflows.OstContextEntity;
import com.ost.ostsdk.workflows.interfaces.OstDeviceRegisteredInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class OstRegisterDeviceTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext);
        new OstSecureKeyModelRepository().deleteAllSecureKeys(new OstTaskCallback() {});
    }

    @Test
    public void testRegisterDeviceVanillaFlow() {
        // Context of the app under test.
        String userId = "1";
        final String[] address = new String[1];
        Looper.prepare();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        OstSdk.registerDevice(userId, new AbsWorkFlowCallback() {
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