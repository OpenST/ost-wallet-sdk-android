package com.ost.ostsdk.workflows;


import android.os.Handler;

import com.ost.ostsdk.workflows.interfaces.OstWorkFlowCallback;

public class OstWorkFlowFactory {

    public OstDeployTokenHolder deployTokenHolder(String uPin, String password, OstWorkFlowCallback callback) {
        Handler handler = new Handler();
        final OstDeployTokenHolder ostDeployTokenHolder = new OstDeployTokenHolder(uPin,password, handler, callback);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ostDeployTokenHolder.perform();
            }
        }).start();

        return ostDeployTokenHolder;
    }

    OstDeployTokenHolder QRCodeInput() {
        OstDeployTokenHolder  ostDeployTokenHolder = null;
        return ostDeployTokenHolder;
    }
}
