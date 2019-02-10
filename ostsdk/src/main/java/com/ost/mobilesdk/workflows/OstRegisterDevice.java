package com.ost.mobilesdk.workflows;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.DispatchAsync;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class OstRegisterDevice implements OstDeviceRegisteredInterface {

    private static final String TAG = "OstRegisterDevice";
    private final String mUserId;
    private final Handler mHandler;
    private final OstWorkFlowCallback mCallback;

    private enum STATES {
        INIT,
        ERROR,
        REGISTERED,
    }

    private STATES mCurrentState = STATES.INIT;
    private Object mStateObject = null;

    private void setCurrentState(STATES currentState, Object stateObject) {
        this.mCurrentState = currentState;
        this.mStateObject = stateObject;
    }

    public OstRegisterDevice(String userId, Handler handler, OstWorkFlowCallback callback) {
        mUserId = userId;
        mHandler = handler;
        mCallback = callback;
    }

    synchronized public void perform() {
        DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public void execute() {
                switch (mCurrentState) {
                    case INIT:
                        Log.d(TAG, String.format("Workflow for userId: %s started", mUserId));

                        Log.i(TAG, "Validating user Id");
                        if (!isValidUserId()) {
                            postError(String.format("Invalid UserId: %s", mUserId));
                            return;
                        }

                        Log.i(TAG, "Creating current device if does not exist");
                        OstDevice ostDevice = createCurrentDevice();
                        if (null == ostDevice) {
                            postError(String.format("Ost device creation error for user Id: %s", mUserId));
                            return;
                        }

                        Log.i(TAG, "Check is device registered");
                        if (!hasRegisteredDevice(ostDevice)) {
                            Log.i(TAG, "Registering device");
                            registerDevice(ostDevice);
                            return;
                        }
                        postFlowComplete();
                        break;
                    case REGISTERED:
                        Log.i(TAG, "Device registered");
                        JSONObject apiResponse = (JSONObject) mStateObject;
                        try {
                            OstSdk.parse(apiResponse);
                        } catch (JSONException e) {
                            postError(String.format("Register device api response parsing error: %s", mUserId));
                            return;
                        }
                        postFlowComplete();
                        break;
                    case ERROR:
                        postError(String.format("Error in Registration flow: %s", mUserId));
                        break;
                }
            }
        });
    }

    private void postFlowComplete() {
        Log.i(TAG, "Flow complete");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.flowComplete(new OstContextEntity());
            }
        });
    }

    private void registerDevice(OstDevice ostDevice) {
        final JSONObject apiResponse = buildApiResponse(ostDevice);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.registerDevice(apiResponse, OstRegisterDevice.this);
            }
        });
    }

    private void postError(String msg) {
        Log.i(TAG, "Flow Error");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.flowInterrupt(new OstError(msg));
            }
        });
    }

    private OstDevice createCurrentDevice() {
        OstDevice ostDevice = null;
        try {
            OstUser ostUser = OstSdk.initUser(mUserId);
            ostDevice = ostUser.getCurrentDevice();
            if (null == ostDevice) {
                ostDevice = ostUser.createDevice();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Ost User init exception");
        }
        return ostDevice;
    }

    private boolean isValidUserId() {
        return !TextUtils.isEmpty(mUserId);
    }

    private boolean hasRegisteredDevice(OstDevice ostDevice) {
        OstUser ostUser = OstSdk.getUser(mUserId);
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        return ostKeyManager.getApiKeyAddress().equalsIgnoreCase(ostDevice.getPersonalSignAddress())
                && OstDevice.CONST_STATUS.REGISTERED.equals(ostDevice.getStatus());

    }

    private JSONObject buildApiResponse(OstDevice ostDevice) {
        JSONObject jsonObject = new JSONObject();
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
    public void cancelFlow(OstError ostError) {
        setCurrentState(STATES.ERROR, ostError);
        perform();
    }

    @Override
    public void deviceRegistered(JSONObject apiResponse) {
        setCurrentState(STATES.REGISTERED,apiResponse);
        perform();
    }
}