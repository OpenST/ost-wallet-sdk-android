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
import com.ost.walletsdk.network.OstApiError;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.entity.OstLogEvent;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.util.CommonUtils;
import ost.com.demoapp.util.DBLog;

import static ost.com.demoapp.sdkInteract.SdkInteract.CALLBACK_TYPE;

public class WorkFlowListener implements OstWorkFlowCallback {

    private static final String LOG_TAG = "OstWorkFlowListener";
    private final SdkInteract mSdkInteract;

    private static long identifier = 0;
    private final DBLog dbLogger;

    public long getId() {
        return mId;
    }

    private final long mId;

    WorkFlowListener() {
        this.mSdkInteract = SdkInteract.getInstance();
        this.dbLogger = AppProvider.get().getDBLogger();
        this.mId = identifier++;
    }

    @Override
    public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {
        Log.i(LOG_TAG, String.format("Device Object %s ", apiParams.toString()));
        //Store event in db
        dbLogger.log(new OstLogEvent(getId(),
                OstWorkflowContext.WORKFLOW_TYPE.SETUP_DEVICE.toString(),
                "registerDevice",
                apiParams.toString()));

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
        //Store event in db
        dbLogger.log(new OstLogEvent(getId(),
                ostWorkflowContext.getWorkflow_type().toString(),
                "getPin",
                ""));

        mSdkInteract.getPinCallbackListener().getPin(getId(), ostWorkflowContext, userId, ostPinAcceptInterface);
    }

    @Override
    public void invalidPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        Log.d(LOG_TAG, String.format("Invalid Pin: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));
        //Store event in db
        dbLogger.log(new OstLogEvent(getId(),
                ostWorkflowContext.getWorkflow_type().toString(),
                "invalidPin",
                ""));

        mSdkInteract.getPinCallbackListener().invalidPin(getId(), ostWorkflowContext, userId, ostPinAcceptInterface);
    }

    @Override
    public void pinValidated(OstWorkflowContext ostWorkflowContext, String userId) {
        Log.d(LOG_TAG, String.format("Pin Validated: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));
        //Store event in db
        dbLogger.log(new OstLogEvent(getId(),
                ostWorkflowContext.getWorkflow_type().toString(),
                "pinValidated",
                ""));

        mSdkInteract.getPinCallbackListener().pinValidated(getId(), ostWorkflowContext, userId);
    }

    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d(LOG_TAG, String.format("Flow Complete: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));
        //Store event in db
        String completeString = String.format("Entity %s ",
                ostWorkflowContext.getWorkflow_type(), null == ostContextEntity ? "null" : ostContextEntity.getEntityType());
        dbLogger.log(new OstLogEvent(getId(),
                ostWorkflowContext.getWorkflow_type().toString(),
                "flowComplete",
                completeString));

        mSdkInteract.notifyEvent(getId(), CALLBACK_TYPE.FLOW_COMPLETE, ostWorkflowContext, ostContextEntity);
        mSdkInteract.unRegister(this);
    }

    @Override
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        Log.d(LOG_TAG, String.format("Flow Interrupted: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));

        //region Sdk Error Handling
        if (OstErrors.ErrorCode.WORKFLOW_CANCELLED == ostError.getErrorCode()) {
            //Test-App has cancelled the workflow
            Log.i(LOG_TAG, "Interrupt Reason: Test-App cancelled the workflow.");
            //Store event in db
            dbLogger.log(new OstLogEvent(getId(),
                    ostWorkflowContext.getWorkflow_type().toString(),
                    "flowInterrupt",
                    "Interrupt Reason: Test-App cancelled the workflow."));
        } else if (ostError.isApiError()) {
            OstApiError apiError = (OstApiError) ostError;
            if (apiError.isApiSignerUnauthorized()) {
                // The device has been revoked and can not make any more calls to OST Platform.
                // Apps must logout users at this point.
                // For purpose of testing the sdk, lets give users an option.
                deviceUnauthorized(apiError);
            } else {
                //Let's log the error
                logSdkError(ostWorkflowContext, ostError);
            }
        } else if (OstErrors.ErrorCode.DEVICE_NOT_SETUP == ostError.getErrorCode()) {
            // Device needs to be registered or new device keys need to be created.
            // To perform this operation, Test-App needs to call OstSdk.setupDevice()
            // If app believe that user is authenticated, they should logout user here.
            deviceUnauthorized(ostError);
        } else {
            //Let's log the error
            logSdkError(ostWorkflowContext, ostError);
        }

        mSdkInteract.notifyEvent(getId(), CALLBACK_TYPE.FLOW_INTERRUPT, ostWorkflowContext, ostError);
        mSdkInteract.unRegister(this);
    }

    @Override
    public void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d(LOG_TAG, String.format("Request Acknowledged: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));
        //Store event in db
        dbLogger.log(new OstLogEvent(getId(),
                ostWorkflowContext.getWorkflow_type().toString(),
                "requestAcknowledged",
                String.format("Entity type: %s",
                        null == ostContextEntity ? "null" : ostContextEntity.getEntityType())));

        mSdkInteract.notifyEvent(getId(), CALLBACK_TYPE.REQUEST_ACK, ostWorkflowContext, ostContextEntity);
    }

    @Override
    public void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {
        Log.d(LOG_TAG, String.format("Verify Data: WorkFlow Id: %d of Workflow %s", getId(), ostWorkflowContext.getWorkflow_type().toString()));
        //Store event in db
        dbLogger.log(new OstLogEvent(getId(),
                ostWorkflowContext.getWorkflow_type().toString(),
                "verifyData",
                String.format("Verify data: %s",
                        (null == ostContextEntity ? new JSONObject() : ostContextEntity.getEntity()).toString()
                )));

        mSdkInteract.getVerifyDataCallbackListener().verifyData(getId(), ostWorkflowContext, ostContextEntity, ostVerifyDataInterface);
    }


    private void logSdkError(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        StringBuilder errorStringBuilder = new StringBuilder();

        String errorString = String.format("Error: %s " +
                        "\nwith error code: %s" +
                        "\ninternal error code: %s",
                ostError.getMessage(),
                ostError.getErrorCode(),
                ostError.getInternalErrorCode()
        );
        errorStringBuilder.append(errorString);

        if (ostError.isApiError()) {
            OstApiError ostApiError = ((OstApiError) ostError);
            String apiErrorCodeMsg = String.format(
                    "\n%s: %s",
                    ostApiError.getErrCode(),
                    ostApiError.getErrMsg());

            errorStringBuilder.append(apiErrorCodeMsg);

            errorStringBuilder.append(
                    String.format("\napi_internal_id: %s", ostApiError.getApiInternalId())
            );

            List<OstApiError.ApiErrorData> apiErrorDataList = ostApiError.getErrorData();
            for (OstApiError.ApiErrorData apiErrorData : apiErrorDataList) {
                String errorData = String.format(
                        "\n%s: %s",
                        apiErrorData.getParameter(),
                        apiErrorData.getMsg());

                errorStringBuilder.append(errorData);
            }
        }
        String stringFormattedError = errorStringBuilder.toString();
        Log.e(LOG_TAG, stringFormattedError);

        //Store event in db
        dbLogger.log(new OstLogEvent(getId(),
                ostWorkflowContext.getWorkflow_type().toString(),
                "flowInterrupt",
                stringFormattedError));
    }

    private void deviceUnauthorized(OstError ostError) {
        String title = "Device not registered";
        String message = "Please login again to register your device.";
        if (ostError.isApiError()) {
            OstApiError apiError = (OstApiError) ostError;
            if (apiError.isApiSignerUnauthorized()) {
                title = "Device Revoked";
            }
        }
        Log.e(LOG_TAG, title);
        // Todo:: show dialog to relaunch application
    }
}