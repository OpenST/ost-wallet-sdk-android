package com.ost.ostsdk.workflow;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.ost.ostsdk.OstSdk;

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

    }
}