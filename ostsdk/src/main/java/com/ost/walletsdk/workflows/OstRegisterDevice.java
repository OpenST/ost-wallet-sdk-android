/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.workflows;

import android.text.TextUtils;
import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstToken;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.walletsdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * To Register current device on OST Platform through App
 */
public class OstRegisterDevice extends OstBaseWorkFlow implements OstDeviceRegisteredInterface {

    private static final String TAG = "OstRegisterDevice";
    private final boolean mForceSync;
    private final String mTokenId;

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.SETUP_DEVICE;
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
                ostUser = OstUser.init(mUserId, mTokenId);
                OstToken.init(mTokenId);

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

                // Verify Device Registration before sync.
                AsyncStatus status = verifyDeviceRegistered();

                //Sync if needed.
                if ( status.isSuccess() ) {
                    sync();
                }

                //Lets verify if device was registered.
                return status;

            case REGISTERED:
                Log.i(TAG, "Device registered");
                // Verify Device Registration before sync.
                AsyncStatus verificationStatus = verifyDeviceRegistered();

                if ( verificationStatus.isSuccess() ) {
                    //Sync Registered Entities.
                    syncRegisteredEntities();
                }

                //Lets verify if device was registered.
                return verificationStatus;

            case CANCELED:
                return postErrorInterrupt("wf_rd_pr_3" , ErrorCode.WORKFLOW_CANCELLED);
        }
        return new AsyncStatus(true);
    }

    private void syncRegisteredEntities() {
        Log.i(TAG, "Syncing registered entities.");
        //To-Do: [Future] Check if we really need to sync device here.
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
                OstWorkFlowCallback callback = getCallback();
                if ( null != callback ) {
                    callback.registerDevice(apiResponse, OstRegisterDevice.this);
                } else {
                    //Do Nothing, let the workflow die.
                }
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
    public void cancelFlow() {
        setFlowState(OstRegisterDevice.STATES.CANCELED, null);
        perform();
    }

    private AsyncStatus verifyDeviceRegistered() {
        try {
            //Just sync current device.
            syncCurrentDevice();

            //Get the currentDevice
            OstUser ostUser = OstUser.getById(mUserId);
            OstDevice device = ostUser.getCurrentDevice();

            //Forward it.
            return postFlowComplete( new OstContextEntity(
                device,
                OstSdk.DEVICE
            ));

        } catch (OstError error) {
            //This could happen.
            return postErrorInterrupt( error );
        } catch (Exception ex) {
            //Catch all unexpected errors.
            OstError error = new OstError("wf_rd_vdr_1", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            error.setStackTrace( ex.getStackTrace() );
            Log.e("Ost-Rachin", "-----------------------------------------");
            ex.printStackTrace();
            Log.e("Ost-Rachin", "-----------------------------------------");
            return postErrorInterrupt( error );
        }
    }
}