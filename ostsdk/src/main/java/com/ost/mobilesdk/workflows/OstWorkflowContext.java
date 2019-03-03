package com.ost.mobilesdk.workflows;

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
        PIN_RESET
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