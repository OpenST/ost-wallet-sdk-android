package com.ost.walletsdk.ui.loader;

import com.ost.walletsdk.workflows.OstWorkflowContext;

public class OstSdkWorkflowLoader implements OstLoaderDelegate {

    @Override
    public LoaderFragment getLoader(OstWorkflowContext.WORKFLOW_TYPE workflowType) {
        return new LoaderFragment();
    }

    @Override
    public boolean waitForFinalization(OstWorkflowContext.WORKFLOW_TYPE workflowType) {
        return false;
    }
}