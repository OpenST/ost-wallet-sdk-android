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

import com.ost.walletsdk.annotations.NonNull;
import android.util.Log;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.workflows.OstWorkflowContext.WORKFLOW_TYPE;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.ost.walletsdk.workflows.WorkflowStateManager.DATA_VERIFIED;
import static com.ost.walletsdk.workflows.WorkflowStateManager.PARAMS_VALIDATED;

/**
 * It perform workflow operations by reading QR data.
 * QR data should be passes as JSON object in the constructor
 * {@link #OstPerform(String, JSONObject, OstWorkFlowCallback)}
 * It can perform Execute Rule Transactions, Add Device and Revoke Device.
 */
public class OstPerform extends OstBaseWorkFlow implements OstVerifyDataInterface {

    private static final String TAG = "OstPerform";
    private JSONObject mPayload;
    private final String mStringPayload;
    private String qrVersion;
    private DataDefinitionInstance dataDefinitionInstance;

    @Deprecated
    public OstPerform(String userId, JSONObject payload, OstWorkFlowCallback callback) {
        super(userId, callback);
        mPayload = payload;
        mStringPayload = "";
        this.populateVersion();
    }

    public OstPerform(String userId, String payload, OstWorkFlowCallback callback) {
        super(userId, callback);
        mStringPayload = payload;
        this.populatePayload();
        this.populateVersion();
    }

    private void populatePayload() {
        if ( null == mStringPayload ) {
            return;
        }
        try {
            populateV1Payload();
            return;
        } catch (JSONException ex) {
            // ignore. Lets try v2.
        }

        try {
            populateV2Payload();
            return; /* DO NOT REMOVE THIS RETURN. DEVS WILL GET INTO TROUBLE IF V3 COMES */
        } catch (Throwable th) {
            // ignore. Validate Payload shall take care of it.
        }
    }

    private void populateV1Payload() throws JSONException {
        mPayload = new JSONObject( mStringPayload );
    }

    private void populateV2Payload() {
        // V2-Format: Data-Definition|Data-Definition-Version|...

        // Split by delimiter
        String[] parts = mStringPayload.split("\\" + OstConstants.QR_V2_DELIMITER);

        // Must have at-least 3 parts
        if ( parts.length < 3) {
            return;
        }
        try {
            mPayload = new JSONObject();

            //Data-Definition
            mPayload.putOpt(OstConstants.QR_DATA_DEFINITION, parts[0]);

            //Data-Definition-Version
            mPayload.putOpt(OstConstants.QR_DATA_DEFINITION_VERSION, parts[1]);

            //data
            JSONObject dataObject = new JSONObject();
            mPayload.putOpt(OstConstants.QR_DATA, dataObject);

            dataObject.putOpt(OstConstants.QR_V2_INPUT, mStringPayload);
        } catch (JSONException e) {
            mPayload = null;
        }
    }

    private void populateVersion() {
        if ( null == mPayload) {
            return;
        }
        qrVersion = mPayload.optString(OstConstants.QR_DATA_DEFINITION_VERSION);
    }



                      @Override
    protected void setStateManager() {
        super.setStateManager();
        ArrayList<String> orderedStates = stateManager.orderedStates;
        int paramsValidationIndx = orderedStates.indexOf(PARAMS_VALIDATED);
        //Add custom states.
        orderedStates.add(paramsValidationIndx + 1, WorkflowStateManager.VERIFY_DATA);
        orderedStates.add(paramsValidationIndx + 2, WorkflowStateManager.DATA_VERIFIED);
    }


    @Override
    protected AsyncStatus onStateChanged(String state, Object stateObject) {
        try {
            switch (state) {
                case WorkflowStateManager.PARAMS_VALIDATED:
                    dataDefinitionInstance.validateApiDependentParams();
                    return performNext();
                case WorkflowStateManager.VERIFY_DATA:
                    OstContextEntity ostContextEntity = dataDefinitionInstance.getContextEntity();
                    postVerifyData(
                            dataDefinitionInstance.getWorkFlowType(),
                            ostContextEntity,
                            OstPerform.this
                    );
                    return new AsyncStatus(true);
                case WorkflowStateManager.DATA_VERIFIED:
                    dataDefinitionInstance.startDataDefinitionFlow();
                    return new AsyncStatus(true);
            }
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        } catch (Throwable th) {
            OstError ostError = new OstError("bua_wf_op_1", OstErrors.ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            ostError.setStackTrace(th.getStackTrace());
            return postErrorInterrupt(ostError);
        }
        return super.onStateChanged(state, stateObject);
    }

    @Override
    void ensureValidParams() {
        super.ensureValidParams();

        // Set the qrVersion.
        validatePayload();

        if ( qrVersion.startsWith("2.") ) {
            dataDefinitionInstance = getV2DataDefinitionInstance();
        } else {
            dataDefinitionInstance = getV1DataDefinitionInstance();
        }

        dataDefinitionInstance.validateDataPayload();
        dataDefinitionInstance.validateDataParams();
    }



    private DataDefinitionInstance getV1DataDefinitionInstance() {
        String dataDefinition = getDataDefinition();
        JSONObject dataObject = getDataObject();

        if (OstConstants.DATA_DEFINITION_TRANSACTION.equalsIgnoreCase(dataDefinition)) {
            JSONObject metaObject = getMetaObject();
            return new OstExecuteTransaction.TransactionDataDefinitionInstance(
                    dataObject,
                    metaObject,
                    mUserId,
                    getCallback());
        } else if (OstConstants.DATA_DEFINITION_AUTHORIZE_DEVICE.equalsIgnoreCase(dataDefinition)) {
            return new OstAddDeviceWithQR.AddDeviceDataDefinitionInstance(
                    dataObject,
                    mUserId,
                    getCallback());
        } else if (OstConstants.DATA_DEFINITION_REVOKE_DEVICE.equalsIgnoreCase(dataDefinition)) {
            return new OstRevokeDevice.RevokeDeviceDataDefinitionInstance(
                    dataObject,
                    mUserId,
                    getCallback());
        } else if (OstConstants.DATA_DEFINITION_AUTHORIZE_SESSION.equalsIgnoreCase(dataDefinition)) {
            return new OstAddSessionWithQR.AddSessionDataDefinitionInstance(
                    dataObject,
                    qrVersion,
                    mUserId,
                    getCallback());
        } else {
            throw new OstError("wf_pe_pr_gv1_ddi_1", OstErrors.ErrorCode.INVALID_QR_CODE);
        }
    }

    private DataDefinitionInstance getV2DataDefinitionInstance() {
        String dataDefinition = getDataDefinition();
        if (OstConstants.DATA_DEFINITION_AUTHORIZE_SESSION.equalsIgnoreCase(dataDefinition)) {
            return OstAddSessionWithQR.AddSessionDataDefinitionInstance.fromV2QR(
                    mStringPayload,
                    mUserId,
                    getCallback());
        } else {
            throw new OstError("wf_pe_pr_gv2_ddi_1", OstErrors.ErrorCode.INVALID_QR_CODE);
        }
    }

    private JSONObject getMetaObject() {
        JSONObject jsonObject = mPayload.optJSONObject(OstConstants.QR_META);
        if (null == jsonObject) {
            return new JSONObject();
        }
        return jsonObject;
    }

    @Override
    public void dataVerified() {
        stateManager.setState(DATA_VERIFIED);
        perform();
    }

    private void validatePayload() {
        if (null == mPayload) {
            throw new OstError("wf_pe_pr_vp_1", OstErrors.ErrorCode.INVALID_QR_CODE);
        }

        if ( null == qrVersion ) {
            throw new OstError("wf_pe_pr_vp_2", OstErrors.ErrorCode.INVALID_QR_CODE);
        }

        boolean hasDataDefinition = mPayload.has(OstConstants.QR_DATA_DEFINITION);
        boolean data = mPayload.has(OstConstants.QR_DATA);
        if (!(hasDataDefinition && data)) {
            throw new OstError("wf_pe_pr_vp_2", OstErrors.ErrorCode.INVALID_QR_CODE);
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
        return OstWorkflowContext.WORKFLOW_TYPE.PERFORM_QR_ACTION;
    }

    interface DataDefinitionInstance {
        void validateDataPayload();

        void validateDataParams();

        OstContextEntity getContextEntity();

        void startDataDefinitionFlow();

        void validateApiDependentParams();

        WORKFLOW_TYPE getWorkFlowType();
    }
}