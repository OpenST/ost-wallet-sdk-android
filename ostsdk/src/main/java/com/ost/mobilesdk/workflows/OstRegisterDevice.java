package com.ost.mobilesdk.workflows;

import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;
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

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.REGISTER_DEVICE;
    }

    private enum STATES {
        INITIAL,
        CANCELED,
        REGISTERED,
    }

    private STATES mCurrentState = STATES.INITIAL;
    private Object mStateObject = null;

    private void setFlowState(STATES currentState, Object stateObject) {
        this.mCurrentState = currentState;
        this.mStateObject = stateObject;
    }

    public OstRegisterDevice(String userId, String tokenId, boolean forceSync, OstWorkFlowCallback callback) {
        super(userId, callback);

        mTokenId = tokenId;
        mForceSync = forceSync;
    }

    synchronized public AsyncStatus process() {
        switch (mCurrentState) {
            case INITIAL:
                Log.d(TAG, String.format("Workflow for userId: %s started", mUserId));

                Log.i(TAG, "Validating user Id");
                if (!hasValidParams()) {
                    return postErrorInterrupt("wf_rd_pr_1" , ErrorCode.INVALID_WORKFLOW_PARAMS);
                }

                Log.i(TAG, "Initializing User and Token");
                OstUser ostUser;
                ostUser = OstUser.initUser(mUserId, mTokenId);
                OstSdk.initToken(mTokenId);

                Log.i(TAG, "Creating current device if does not exist");
                OstDevice ostDevice = createOrGetCurrentDevice(ostUser);
                if (null == ostDevice) {
                    return postErrorInterrupt("wf_rd_pr_2" , ErrorCode.CREATE_DEVICE_FAILED);
                }

                Log.i(TAG, "Check we are able to access device keys");
                if (!hasDeviceApiKey(ostDevice)) {
                    return postErrorInterrupt("wf_rd_pr_3", ErrorCode.CREATE_DEVICE_FAILED);
                }

                Log.i(TAG, "Check if device has been registered.");
                if (OstDevice.CONST_STATUS.CREATED.equalsIgnoreCase( ostDevice.getStatus() )  ) {
                    Log.i(TAG, "Registering device");
                    registerDevice(ostDevice);
                    return new AsyncStatus(true);
                }
                Log.i(TAG, "Device is already registered. ostDevice.status:" + ostDevice.getStatus() );
                sync();
                postFlowComplete();
                break;

            case REGISTERED:
                Log.i(TAG, "Device registered");
                syncRegisteredEntities();
                postFlowComplete();
                break;

            case CANCELED:
                return postErrorInterrupt("wf_rd_pr_3" , ErrorCode.WORKFLOW_CANCELED);
        }
        return new AsyncStatus(true);
    }

    private void syncRegisteredEntities() {
        Log.i(TAG, "Syncing registered entities.");
        new OstSdkSync(mUserId, OstSdkSync.SYNC_ENTITY.USER, OstSdkSync.SYNC_ENTITY.DEVICE, OstSdkSync.SYNC_ENTITY.TOKEN).perform();
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
        setFlowState(OstRegisterDevice.STATES.CANCELED, ostError);
        perform();
    }
}