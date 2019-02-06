package com.ost.mobilesdk.workflows;

import android.os.Handler;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.DispatchAsync;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class OstRegisterDevice implements OstDeviceRegisteredInterface {

    private static final String TAG = "OstRegisterDevice";
    private final String mUserId;
    private final Handler mHandler;
    private final OstWorkFlowCallback mCallback;

    public OstRegisterDevice(String userId, Handler handler, OstWorkFlowCallback callback) {
        mUserId = userId;
        mHandler = handler;
        mCallback = callback;
    }

    public void perform() {
        DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public void execute() {
                if (hasRegisteredDevice()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.flowComplete(new OstContextEntity());
                        }
                    });
                } else {
                    final JSONObject apiResponse = buildApiResponse();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.registerDevice(apiResponse, OstRegisterDevice.this);
                        }
                    });
                }
            }
        });
    }

    private boolean hasRegisteredDevice() {
        OstDevice[] ostDevices = OstDevice.getDevicesByParentId(mUserId);
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        for (OstDevice device : ostDevices) {
            if (ostKeyManager.getApiKeyAddress().equalsIgnoreCase(device.getPersonalSignAddress())
                    && OstDevice.CONST_STATUS.REGISTERED.equals(device.getStatus())) {
                return true;
            }
        }
        return false;
    }

    private JSONObject buildApiResponse() {
        JSONObject jsonObject = new JSONObject();
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        String apiAddress = ostKeyManager.getApiKeyAddress();
        String address = ostKeyManager.createKey();
        OstDevice ostDevice = OstDevice.init(address, apiAddress, mUserId);
        if (null == ostDevice) {
            return jsonObject;

        } else {
            try {
                jsonObject.put(OstSdk.DEVICE, ostDevice.getData());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }

    @Override
    public void cancelFlow(String cancelReason) {

    }

    @Override
    public void deviceRegistered(JSONObject apiResponse) {
        try {
            OstSdk.parse(apiResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mCallback.flowComplete(new OstContextEntity());
    }
}