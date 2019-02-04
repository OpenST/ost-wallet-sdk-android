package com.ost.ostsdk.workflow;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.workflows.OstContextEntity;
import com.ost.ostsdk.workflows.OstError;

import org.junit.BeforeClass;
import org.junit.Test;

public class OstDeployTokenHolderTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext);
        new OstSecureKeyModelRepository().deleteAllSecureKeys(new OstTaskCallback() {});
    }

    @Test
    public void testDeployTokenHolder() {
        // Context of the app under test.
        String uPin = "123456";
        String password = "password";
        boolean isBiometricNeeded = false;
        String userId = "1";
        String tokenId = "1";

        OstSdk.deployTokenHolder(userId, tokenId, uPin, password, isBiometricNeeded ,new AbsWorkFlowCallback() {
            @Override
            public void flowComplete(OstContextEntity ostContextEntity) {
                super.flowComplete(ostContextEntity);
            }

            @Override
            public void flowInterrupt(OstError ostError) {
                super.flowInterrupt(ostError);
            }
        });

    }
}