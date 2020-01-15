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

/**
 * WorkflowContext to hold work flow type
 */
public class OstWorkflowContext {
    public enum WORKFLOW_TYPE {
        UNKNOWN,
        SETUP_DEVICE,
        ACTIVATE_USER,
        ADD_SESSION,
        GET_DEVICE_MNEMONICS,
        UPDATE_BIOMETRIC_PREFERENCE,
        PERFORM_QR_ACTION,
        SHOW_DEVICE_QR,
        EXECUTE_TRANSACTION,
        AUTHORIZE_DEVICE_WITH_QR_CODE,
        AUTHORIZE_DEVICE_WITH_MNEMONICS,
        INITIATE_DEVICE_RECOVERY,
        ABORT_DEVICE_RECOVERY,
        REVOKE_DEVICE,
        RESET_PIN,
        LOGOUT_ALL_SESSIONS
    }
    private WORKFLOW_TYPE workflow_type;
    private String workflowId;

    public OstWorkflowContext(WORKFLOW_TYPE workflow_type) {
        this.workflow_type = workflow_type;
        this.workflowId = "UNDEFINED";
    }

    public OstWorkflowContext(@NonNull String workflowId, @NonNull WORKFLOW_TYPE workflow_type) {
        this.workflow_type = workflow_type;
        this.workflowId = workflowId;
    }

    public OstWorkflowContext() {
        this.workflow_type = WORKFLOW_TYPE.UNKNOWN;
    }

    /**
     * @deprecated
     * Use {@link #getWorkflowType()} instead
     */
    public WORKFLOW_TYPE getWorkflow_type() {
        return workflow_type;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    //Added so that getWorkflow_type can be deprecated in future releases.
    public WORKFLOW_TYPE getWorkflowType() {
        return workflow_type;
    }
}