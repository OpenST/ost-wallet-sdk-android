package com.ost.walletsdk.ui.loader;

import com.ost.walletsdk.workflows.OstWorkflowContext;

public interface OstLoaderDelegate {
    LoaderFragment getLoader(OstWorkflowContext.WORKFLOW_TYPE workflowType);

    boolean waitForFinalization(OstWorkflowContext.WORKFLOW_TYPE workflowType);
}