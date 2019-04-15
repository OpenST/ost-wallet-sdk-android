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
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.walletsdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    //region - Flow methods
    @Override
    protected boolean shouldAskForAuthentication() {
        return false;
    }

    @Override
    boolean shouldCheckCurrentDeviceAuthorization() {
        return false;
    }
    //endregion

    @Override
    protected void setStateManager() {
        super.setStateManager();
        List<String> orderedStates = stateManager.orderedStates;
        int paramsValidationIndx = orderedStates.indexOf(WorkflowStateManager.PARAMS_VALIDATED);
        //Add custom states.
        List<String> customStates = new ArrayList<>();
        customStates.add(WorkflowStateManager.INITIALIZED);
        customStates.add(WorkflowStateManager.REGISTERED);

        orderedStates.addAll(paramsValidationIndx, customStates);
    }

    public OstRegisterDevice(String userId, String tokenId, boolean forceSync, OstWorkFlowCallback callback) {
        super(userId, callback);

        mTokenId = tokenId;
        mForceSync = forceSync;
    }

    @Override
    void ensureValidParams() {
        super.ensureValidParams();
        Log.i(TAG, "Validating user and token Id");
        if (!hasValidParams()) {
            throw new OstError("wf_rd_evp_1" , ErrorCode.INVALID_WORKFLOW_PARAMS);
        }
    }

    @Override
    protected AsyncStatus onStateChanged(String state, Object stateObject) {
        try {
            switch (state) {
                case WorkflowStateManager.INITIALIZED:
                    Log.i(TAG, "Initializing User and Token");
                    OstUser ostUser;
                    ostUser = OstUser.init(mUserId, mTokenId);
                    OstToken.init(mTokenId);
                    initApiClient();

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
                        //Forward it.
                        return postFlowComplete(new OstContextEntity(
                                ostUser.getCurrentDevice(),
                                OstSdk.DEVICE
                        ));
                    }

                    //Lets verify if device was registered.
                    return status;
                case WorkflowStateManager.REGISTERED:
                    Log.i(TAG, "Device registered");
                    // Verify Device Registration before sync.
                    AsyncStatus verificationStatus = verifyDeviceRegistered();

                    if ( verificationStatus.isSuccess() ) {
                        //Sync Registered Entities.
                        ensureApiCommunication();
                        ensureOstUser(true);
                        ensureOstToken();
                        //Forward it.
                        return postFlowComplete(new OstContextEntity(
                                mOstUser.getCurrentDevice(),
                                OstSdk.DEVICE
                        ));
                    }

                    //Lets verify if device was registered.
                    return verificationStatus;
            }
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        } catch (Throwable th) {
            OstError ostError = new OstError("rd_wf_osc_1", OstErrors.ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            ostError.setStackTrace(th.getStackTrace());
            return postErrorInterrupt(ostError);
        }
        return super.onStateChanged(state, stateObject);
    }

    @Override
    public void deviceRegistered(JSONObject apiResponse) {
        performWithState(WorkflowStateManager.REGISTERED, apiResponse);
    }

    //region - Helper methods
    private void sync() {
        Log.i(TAG, String.format("Syncing sdk: %b", mForceSync));
        if (mForceSync) {
            ensureApiCommunication();
            ensureOstUser(true);
            ensureDeviceManager();
            ensureOstToken();
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

    private AsyncStatus verifyDeviceRegistered() {
        try {
            //Just sync current device.
            syncCurrentDevice();

            //Get the currentDevice
            OstUser ostUser = OstUser.getById(mUserId);
            OstDevice device = ostUser.getCurrentDevice();

            if (device.canMakeApiCall()) {
                return new AsyncStatus(true);
            } else {
                throw new OstError("wf_rd_vdr_1", ErrorCode.DEVICE_NOT_REGISTERED);
            }
        } catch (OstError error) {
            //This could happen.
            return postErrorInterrupt( error );
        } catch (Exception ex) {
            //Catch all unexpected errors.
            OstError error = new OstError("wf_rd_vdr_1", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            error.setStackTrace( ex.getStackTrace() );
            return postErrorInterrupt( error );
        }
    }
    //endregion
}