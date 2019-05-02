/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.sdkInteract;

import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.util.CommonUtils;

import static ost.com.demoapp.sdkInteract.SdkInteract.CALLBACK_TYPE;

public class WorkFlowListener implements OstWorkFlowCallback {

    private static final String LOG_TAG = "OstWorkFlowListener";
    private final SdkInteract mSdkInteract;

    private static long identifier = 0;
    public long getId() {
        return mId;
    }

    private final long mId;

    WorkFlowListener() {
        this.mSdkInteract = SdkInteract.getInstance();
        this.mId = identifier++;
    }

    @Override
    public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {
        Log.i(LOG_TAG, String.format("Device Object %s ", apiParams.toString()));
        String deviceAddress;
        String apiSignerAddress;
        try {
            JSONObject deviceObject = apiParams.getJSONObject(OstSdk.DEVICE);
            deviceAddress = deviceObject.getString(OstDevice.ADDRESS);
            apiSignerAddress = deviceObject.getString(OstDevice.API_SIGNER_ADDRESS);
        } catch (JSONException ex) {
            Log.e(LOG_TAG, "JSONException in retrieving device_address and api_signer_address", ex);
            ostDeviceRegisteredInterface.cancelFlow();
            return;
        }

        AppProvider.get().getMappyClient().registerDevice(deviceAddress, apiSignerAddress, new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CommonUtils commonUtils = new CommonUtils();
                if (commonUtils.isValidResponse(jsonObject)) {
                    Log.d(LOG_TAG, String.format("Device Registered JSONResponse: %s", jsonObject.toString()));
                    ostDeviceRegisteredInterface.deviceRegistered(jsonObject);
                } else {
                    Log.d(LOG_TAG, String.format("Device Registration failed JSONResponse: %s", jsonObject.toString()));
                    ostDeviceRegisteredInterface.cancelFlow();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(LOG_TAG, "Failure in register device api request", throwable);
                ostDeviceRegisteredInterface.cancelFlow();
            }
        });

    }

    @Override
    public void getPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        Log.d(LOG_TAG, String.format("Get Pin: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        mSdkInteract.getPinCallbackListener().getPin(getId(), ostWorkflowContext, userId ,ostPinAcceptInterface);
    }

    @Override
    public void invalidPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        Log.d(LOG_TAG, String.format("Invalid Pin: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        mSdkInteract.getPinCallbackListener().invalidPin(getId(), ostWorkflowContext, userId ,ostPinAcceptInterface);
    }

    @Override
    public void pinValidated(OstWorkflowContext ostWorkflowContext, String userId) {
        Log.d(LOG_TAG, String.format("Pin Validated: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        mSdkInteract.getPinCallbackListener().pinValidated(getId(), ostWorkflowContext, userId);
    }

    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d(LOG_TAG, String.format("Flow Complete: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        mSdkInteract.notifyEvent(getId(), CALLBACK_TYPE.FLOW_COMPLETE, ostWorkflowContext, ostContextEntity);
        mSdkInteract.unRegister(this);
    }

    @Override
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        Log.d(LOG_TAG, String.format("Flow Interrupted: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        mSdkInteract.notifyEvent(getId(), CALLBACK_TYPE.FLOW_INTERRUPT, ostWorkflowContext, ostError);
        mSdkInteract.unRegister(this);
    }

    @Override
    public void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d(LOG_TAG, String.format("Request Acknowledged: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        mSdkInteract.notifyEvent(getId(), CALLBACK_TYPE.REQUEST_ACK, ostWorkflowContext, ostContextEntity);
    }

    @Override
    public void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {
        Log.d(LOG_TAG, String.format("Verify Data: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        mSdkInteract.getVerifyDataCallbackListener().verifyData(getId(), ostWorkflowContext, ostContextEntity, ostVerifyDataInterface);
    }
}