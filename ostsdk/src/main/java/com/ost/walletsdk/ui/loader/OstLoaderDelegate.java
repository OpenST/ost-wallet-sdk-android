package com.ost.walletsdk.ui.loader;

import com.ost.walletsdk.workflows.OstWorkflowContext;

public interface OstLoaderDelegate {

    /**
     * Get custom loader to show while workflow is in progress
     * @param workflowType OstWorkflowType
     * @return OstLoaderFragment
     */
    OstLoaderFragment getLoader(OstWorkflowContext.WORKFLOW_TYPE workflowType);

    /**
     * Check whether workflow should wait till finalization
     * @param workflowType OstWorkflowType
     * @return boolean
     */
    boolean waitForFinalization(OstWorkflowContext.WORKFLOW_TYPE workflowType);
}