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
        EXECUTE_TRANSACTION,
        AUTHORIZE_DEVICE_WITH_QR_CODE,
        AUTHORIZE_DEVICE_WITH_MNEMONICS,
        INITIATE_DEVICE_RECOVERY,
        ABORT_DEVICE_RECOVERY,
        REVOKE_DEVICE_WITH_QR_CODE,
        RESET_PIN,
        LOGOUT_ALL_SESSIONS
    }
    private WORKFLOW_TYPE workflow_type;

    public OstWorkflowContext(WORKFLOW_TYPE workflow_type) {
        this.workflow_type = workflow_type;
    }

    public OstWorkflowContext() {
        this.workflow_type = WORKFLOW_TYPE.UNKNOWN;
    }

    public WORKFLOW_TYPE getWorkflow_type() {
        return workflow_type;
    }
}