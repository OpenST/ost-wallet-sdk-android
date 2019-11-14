package com.ost.walletsdk.ui.loader;

import com.ost.walletsdk.workflows.OstWorkflowContext;

public class OstSdkLoaderManager implements OstLoaderDelegate {

    @Override
    public LoaderFragment getLoader(OstWorkflowContext.WORKFLOW_TYPE workflowType) {
        return LoaderFragment.newInstance(workflowType);
    }

    @Override
    public boolean waitForFinalization(OstWorkflowContext.WORKFLOW_TYPE workflowType) {
        return false;
    }
}