/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.workflows;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.ost.mobilesdk.workflows.OstBaseUserAuthenticatorWorkflow.WorkflowStateManager.DATA_VERIFIED;
import static com.ost.mobilesdk.workflows.OstBaseUserAuthenticatorWorkflow.WorkflowStateManager.PARAMS_VALIDATED;
import static com.ost.mobilesdk.workflows.OstBaseUserAuthenticatorWorkflow.WorkflowStateManager.VERIFY_DATA;

/**
 * Performs operations based on payload provided
 */
public class OstPerform extends OstBaseUserAuthenticatorWorkflow implements OstVerifyDataInterface {

    private static final String TAG = "OstPerform";
    private final JSONObject mPayload;
    private DataDefinitionInstance dataDefinitionInstance;


    public OstPerform(String userId, JSONObject payload, OstWorkFlowCallback callback) {
        super(userId, callback);
        mPayload = payload;
    }


    @Override
    protected void setStateManager() {
        super.setStateManager();
        ArrayList<String> orderedStates = stateManager.orderedStates;
        int paramsValidationIndx = orderedStates.indexOf(OstBaseUserAuthenticatorWorkflow.WorkflowStateManager.PARAMS_VALIDATED);
        //Add custom states.
        orderedStates.add(paramsValidationIndx + 1, VERIFY_DATA);
        orderedStates.add(paramsValidationIndx + 2, DATA_VERIFIED);
    }


    @Override
    protected AsyncStatus onStateChanged(String state, Object stateObject) {
        try {
            switch (state) {
                case PARAMS_VALIDATED:
                    dataDefinitionInstance.validateApiDependentParams();
                    return performNext();
                case VERIFY_DATA:
                    OstContextEntity ostContextEntity = dataDefinitionInstance.getContextEntity();
                    postVerifyData(ostContextEntity, OstPerform.this);
                    return new AsyncStatus(true);
                case DATA_VERIFIED:
                    dataDefinitionInstance.startDataDefinitionFlow();
                    return new AsyncStatus(true);
            }
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        } catch (Throwable th) {
            OstError ostError = new OstError("bua_wf_osc_1", OstErrors.ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            ostError.setStackTrace(th.getStackTrace());
            return postErrorInterrupt(ostError);
        }
        return super.onStateChanged(state, stateObject);
    }

    @Override
    void ensureValidParams() {
        super.ensureValidParams();

        validatePayload();
        dataDefinitionInstance = getDataDefinitionInstance();
        dataDefinitionInstance.validateDataPayload();
        dataDefinitionInstance.validateDataParams();
    }

    private DataDefinitionInstance getDataDefinitionInstance() {
        String dataDefinition = getDataDefinition();
        JSONObject dataObject = getDataObject();
        if (OstConstants.DATA_DEFINITION_TRANSACTION.equalsIgnoreCase(dataDefinition)) {
            return new OstExecuteTransaction.TransactionDataDefinitionInstance(dataObject, mUserId, getCallback());
        } else if (OstConstants.DATA_DEFINITION_AUTHORIZE_DEVICE.equalsIgnoreCase(dataDefinition)) {
            return new OstAddDeviceWithQR.AddDeviceDataDefinitionInstance(dataObject, mUserId, getCallback());
        } else if (OstConstants.DATA_DEFINITION_REVOKE_DEVICE.equalsIgnoreCase(dataDefinition)) {
            return new OstRevokeDevice.RevokeDeviceDataDefinitionInstance(dataObject, mUserId, getCallback());
        } else {
            throw new OstError("wf_pe_pr_1", OstErrors.ErrorCode.UNKNOWN_DATA_DEFINITION);
        }
    }

    @Override
    public void dataVerified() {
        stateManager.setState(DATA_VERIFIED);
        perform();
    }

    private void validatePayload() {
        if (null == mPayload) {
            throw new OstError("wf_pe_pr_2", OstErrors.ErrorCode.INVALID_WORKFLOW_PARAMS);
        }
        boolean hasDataDefinition = mPayload.has(OstConstants.QR_DATA_DEFINITION);
        boolean hasDataDefinitionVersion = mPayload.has(OstConstants.QR_DATA_DEFINITION_VERSION);
        boolean data = mPayload.has(OstConstants.QR_DATA);
        if (!(hasDataDefinition && hasDataDefinitionVersion && data)) {
            throw new OstError("wf_pe_pr_3", OstErrors.ErrorCode.INVALID_WORKFLOW_PARAMS);
        }
    }

    private @NonNull
    String getDataDefinition() {
        try {
            return mPayload.getString(OstConstants.QR_DATA_DEFINITION);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected JSONException");
        }
        return "";
    }

    private JSONObject getDataObject() {
        try {
            return mPayload.getJSONObject(OstConstants.QR_DATA);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected JSONException");
        }
        return null;
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.PERFORM;
    }

    interface DataDefinitionInstance {
        void validateDataPayload();

        void validateDataParams();

        OstContextEntity getContextEntity();

        void startDataDefinitionFlow();

        void validateApiDependentParams();
    }
}