package com.ost.walletsdk.ui;

import com.ost.walletsdk.annotations.NonNull;

import com.ost.walletsdk.workflows.OstWorkflowContext;

public class OstUIWorkflowContext extends OstWorkflowContext {
    private final String uiWorkflowId;
    public OstUIWorkflowContext(@NonNull OstWorkflowContext context, @NonNull String uiWorkflowId) {
        super(context.getWorkflowId(), context.getWorkflowType());
        this.uiWorkflowId = uiWorkflowId;
    }

    @Override
    public String getWorkflowId() {
        return this.uiWorkflowId;
    }

    public String getBaseWorkflowId() {
        return super.getWorkflowId();
    }
}
