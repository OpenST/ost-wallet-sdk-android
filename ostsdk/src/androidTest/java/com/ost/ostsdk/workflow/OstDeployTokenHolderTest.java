package com.ost.ostsdk.workflow;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.workflows.OstWorkFlowFactory;
import com.ost.ostsdk.workflows.interfaces.OstDeviceRegisteredInterface;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

public class OstDeployTokenHolderTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext);
    }

    @Test
    public void testDeployTokenHolder() {
        // Context of the app under test.
        String uPin = "123456";
        String password = "password";

        OstWorkFlowFactory.deployTokenHolder(uPin, password, new AbsWorkFlowCallback() {
            @Override
            public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {

            }
        });

    }
}