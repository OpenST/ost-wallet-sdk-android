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

public class OstWorkflowContext {
    public enum WORKFLOW_TYPE {
        UNKNOWN,
        REGISTER_DEVICE,
        ACTIVATE_USER,
        ADD_DEVICE,
        PERFORM,
        GET_PAPER_WALLET,
        ADD_SESSION,
        EXECUTE_TRANSACTION,
        ADD_DEVICE_WITH_QR,
        ADD_DEVICE_WITH_MNEMONICS,
        PIN_RESET,
        RECOVER_DEVICE,
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