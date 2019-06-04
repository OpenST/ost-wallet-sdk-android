/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.sampleostsdkapplication;

import android.util.Log;
import android.widget.Toast;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

public class WorkFlowHelper implements OstWorkFlowCallback {


    private static final String TAG = "WorkFlowHelper";
    
    @Override
    public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {
        // Sdk has created new device keys and asked the app to register them.
        // UserDetailsFragment will take care of it.
    }

    @Override
    public void getPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {

        // This is not expected to happen in UserDetailsFragment as it calls setupDevice workflow only.
        // setupDevice work flow where pin is not needed today.
        // In future, pin MAY BE REQUIRED for certain actions.

        ostPinAcceptInterface.cancelFlow();
    }

    @Override
    public void invalidPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        // This is not expected to happen in UserDetailsFragment as it calls setupDevice workflow only.
        // Today, setupDevice work-flow does need user pin.
        // In future, pin MAY BE REQUIRED for certain actions.
        ostPinAcceptInterface.cancelFlow();
    }

    @Override
    public void pinValidated(OstWorkflowContext ostWorkflowContext, String userId) {
        // This is not expected to happen in UserDetailsFragment as it calls setupDevice workflow only.
        // Today, setupDevice work-flow does not need user pin.
        // In future, pin MAY BE REQUIRED for certain actions.
        Log.i(TAG, "User entered correct pin");
    }

    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d("Workflow", "Inside workflow complete");
        Toast.makeText(OstSdk.getContext(), "Work Flow Successfull", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        Toast.makeText(OstSdk.getContext(), String.format("Work Flow %s Error: %s", ostWorkflowContext.getWorkflow_type(), ostError.getMessage()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        // This is not expected to happen in UserDetailsFragment as it calls setupDevice workflow only.
        // Today, setupDevice work flow does not send POST requests to OST Platform
        Log.i(TAG, "Ost Platform has accepted the request.");
    }

    @Override
    public void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {
        // This is not expected to happen in UserDetailsFragment as it calls setupDevice workflow only.
        // Today, setupDevice does not need data-verification
        // In future, data verification by user may be required for certain actions.
        ostVerifyDataInterface.dataVerified();
    }
}