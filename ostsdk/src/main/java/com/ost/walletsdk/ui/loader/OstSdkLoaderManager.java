package com.ost.walletsdk.ui.loader;

import com.ost.walletsdk.workflows.OstWorkflowContext;

public class OstSdkLoaderManager implements OstLoaderDelegate {

    @Override
    public OstLoaderFragment getLoader(OstWorkflowContext.WORKFLOW_TYPE workflowType) {
        return OstLoaderFragment.newInstance(workflowType);
    }

    @Override
    public boolean waitForFinalization(OstWorkflowContext.WORKFLOW_TYPE workflowType) {
        return false;
    }
}