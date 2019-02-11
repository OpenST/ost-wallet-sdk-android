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
    private final boolean mForceSync;
    private final String mTokenId;

    private enum STATES {
        INIT,
        ERROR,
        REGISTERED,
    }

    private STATES mCurrentState = STATES.INIT;
    private Object mStateObject = null;

    private void setFlowState(STATES currentState, Object stateObject) {
        this.mCurrentState = currentState;
        this.mStateObject = stateObject;
    }

    public OstRegisterDevice(String userId, String tokenId, boolean forceSync, Handler handler, OstWorkFlowCallback callback) {
        mUserId = userId;
        mTokenId = tokenId;
        mForceSync = forceSync;
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
                        if (!hasValidParams()) {
                            postError(String.format("Invalid params for userId : %s", mUserId));
                            return;
                        }

                        OstUser ostUser = null;
                        try {
                            Log.i(TAG, "Initializing User and Token");
                            ostUser = OstSdk.initUser(mUserId, mTokenId);
                            OstSdk.initToken(mTokenId);
                        } catch (JSONException e) {
                            postError("Parsing error of user or token");
                        }

                        Log.i(TAG, "Creating current device if does not exist");
                        OstDevice ostDevice = createOrGetCurrentDevice(ostUser);
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
                        sync();
                        postFlowComplete();
                        break;
                    case REGISTERED:
                        Log.i(TAG, "Device registered");
                        sync();
                        postFlowComplete();
                        break;
                    case ERROR:
                        postError(String.format("Error in Registration flow: %s", mUserId));
                        break;
                }
            }
        });
    }

    private void sync() {
        Log.i(TAG, String.format("Syncing sdk: %b", mForceSync));
        if (mForceSync) {
            new OstSdkSync(mUserId).perform();
        } else {
            //Check data before sync
            new OstSdkSync(mUserId, OstSdkSync.SYNC_ENTITY.USER, OstSdkSync.SYNC_ENTITY.TOKEN, OstSdkSync.SYNC_ENTITY.DEVICE).perform();
        }
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

    private OstDevice createOrGetCurrentDevice(OstUser ostUser) {
        OstDevice ostDevice;
        ostDevice = ostUser.getCurrentDevice();
        if (null == ostDevice) {
            ostDevice = ostUser.createDevice();
        }
        return ostDevice;
    }

    private boolean hasValidParams() {
        return !TextUtils.isEmpty(mUserId) && !TextUtils.isEmpty(mTokenId);
    }

    private boolean hasRegisteredDevice(OstDevice ostDevice) {
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        return ostKeyManager.getApiKeyAddress().equalsIgnoreCase(ostDevice.getPersonalSignAddress())
                && (OstDevice.CONST_STATUS.AUTHORIZED.equals(ostDevice.getStatus()) ||
                OstDevice.CONST_STATUS.AUTHORIZING.equals(ostDevice.getStatus()) ||
                OstDevice.CONST_STATUS.REGISTERED.equals(ostDevice.getStatus()));

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
        setFlowState(STATES.ERROR, ostError);
        perform();
    }

    @Override
    public void deviceRegistered(JSONObject apiResponse) {
        setFlowState(STATES.REGISTERED, apiResponse);
        perform();
    }
}