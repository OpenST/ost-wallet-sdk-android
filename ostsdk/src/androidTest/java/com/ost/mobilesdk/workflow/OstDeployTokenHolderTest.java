package com.ost.mobilesdk.workflow;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.mobilesdk.models.OstTaskCallback;
import com.ost.mobilesdk.workflows.OstContextEntity;
import com.ost.mobilesdk.workflows.errors.OstError;

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

        String expirationHeight = "100000";
        String spendingLimit = "100000";
        OstSdk.deployTokenHolder(userId, uPin, password , expirationHeight, spendingLimit, new AbsWorkFlowCallback() {
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