package com.ost.ostsdk.workflows;


import android.os.Handler;

import com.ost.ostsdk.workflows.interfaces.OstWorkFlowCallback;

public class OstWorkFlowFactory {

    public static void deployTokenHolder(String uPin, String password, OstWorkFlowCallback callback) {
        Handler handler = new Handler();
        final OstDeployTokenHolder ostDeployTokenHolder = new OstDeployTokenHolder(uPin,password, handler, callback);
        ostDeployTokenHolder.perform();
    }

    OstDeployTokenHolder QRCodeInput() {
        OstDeployTokenHolder  ostDeployTokenHolder = null;
        return ostDeployTokenHolder;
    }
}
