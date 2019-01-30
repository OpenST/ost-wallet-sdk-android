package com.ost.ostsdk.workflows;

import android.os.Handler;

import com.ost.ostsdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.ostsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

public class OstRegisterDevice implements OstDeviceRegisteredInterface {

    private static final String TAG = "OstRegisterDevice";

    public OstRegisterDevice(String uPin, String password, Handler handler, OstWorkFlowCallback callback) {

    }

    public void perform() {

    }


    @Override
    public void cancelFlow(String cancelReason) {

    }

    @Override
    public void deviceRegistered(JSONObject apiResponse) {

    }
}