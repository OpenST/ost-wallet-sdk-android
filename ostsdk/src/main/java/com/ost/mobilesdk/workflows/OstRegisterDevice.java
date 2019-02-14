package com.ost.mobilesdk.workflows;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * To Register device on kit through App
 */
public class OstRegisterDevice extends OstBaseWorkFlow implements OstDeviceRegisteredInterface {

    private static final String TAG = "OstRegisterDevice";
    private final boolean mForceSync;
    private final String mTokenId;

    private enum STATES {
        INITIAL,
        ERROR,
        REGISTERED,
    }

    private STATES mCurrentState = STATES.INITIAL;
    private Object mStateObject = null;

    private void setFlowState(STATES currentState, Object stateObject) {
        this.mCurrentState = currentState;
        this.mStateObject = stateObject;
    }

    public OstRegisterDevice(String userId, String tokenId, boolean forceSync, Handler handler, OstWorkFlowCallback callback) {
        super(userId, handler, callback);

        mTokenId = tokenId;
        mForceSync = forceSync;
    }

    synchronized public AsyncStatus process() {
        switch (mCurrentState) {
            case INITIAL:
                Log.d(TAG, String.format("Workflow for userId: %s started", mUserId));

                Log.i(TAG, "Validating user Id");
                if (!hasValidParams()) {
                    postError(String.format("Invalid params for userId : %s", mUserId));
                    return new AsyncStatus(false);
                }

                Log.i(TAG, "Initializing User and Token");
                OstUser ostUser;
                try {
                    ostUser = OstSdk.initUser(mUserId, mTokenId);
                    OstSdk.initToken(mTokenId);
                } catch (JSONException e) {
                    postError("Parsing error of user or token");
                    return new AsyncStatus(false);
                }

                Log.i(TAG, "Creating current device if does not exist");
                OstDevice ostDevice = createOrGetCurrentDevice(ostUser);
                if (null == ostDevice) {
                    postError(String.format("Ost device creation error for user Id: %s", mUserId));
                    return new AsyncStatus(false);
                }

                Log.i(TAG, "Check is device registered");
                if (hasUnRegisteredDevice(ostDevice)) {
                    Log.i(TAG, "Registering device");
                    registerDevice(ostDevice);
                    return new AsyncStatus(true);
                }
                sync();
                postFlowComplete();
                break;

            case REGISTERED:
                Log.i(TAG, "Device registered");
                syncRegisteredEntities();
                postFlowComplete();
                break;

            case ERROR:
                postError(String.format("Error in Registration flow: %s", mUserId));
                break;
        }
        return new AsyncStatus(true);
    }

    private void syncRegisteredEntities() {
        Log.i(TAG, "Syncing registered entities.");
        new OstSdkSync(mUserId, OstSdkSync.SYNC_ENTITY.USER, OstSdkSync.SYNC_ENTITY.DEVICE).perform();
    }

    private void sync() {
        Log.i(TAG, String.format("Syncing sdk: %b", mForceSync));
        if (mForceSync) {
            new OstSdkSync(mUserId).perform();
        }
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

    private OstDevice createOrGetCurrentDevice(OstUser ostUser) {
        OstDevice ostDevice;
        ostDevice = ostUser.getCurrentDevice();
        if (null == ostDevice) {
            Log.d(TAG, "currentDevice is null");
            ostDevice = ostUser.createDevice();
        }
        return ostDevice;
    }

    boolean hasValidParams() {
        return super.hasValidParams() && !TextUtils.isEmpty(mTokenId);
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
    public void deviceRegistered(JSONObject apiResponse) {
        setFlowState(STATES.REGISTERED, apiResponse);
        perform();
    }

    @Override
    public void cancelFlow(OstError ostError) {
        setFlowState(OstRegisterDevice.STATES.ERROR, ostError);
        perform();
    }
}