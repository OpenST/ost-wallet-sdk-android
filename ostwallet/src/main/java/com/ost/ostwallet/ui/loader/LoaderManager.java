package com.ost.ostwallet.ui.loader;

import com.ost.walletsdk.ui.loader.LoaderFragment;
import com.ost.walletsdk.ui.loader.OstLoaderDelegate;
import com.ost.walletsdk.workflows.OstWorkflowContext;

public class LoaderManager implements OstLoaderDelegate {

    static LoaderManager INSTANCE = new LoaderManager();
    private LoaderManager(){

    }

    public static OstLoaderDelegate getInstance() {
        return INSTANCE;
    }

    @Override
    public LoaderFragment getLoader(OstWorkflowContext.WORKFLOW_TYPE workflowType) {
        return AppLoaderFragment.newInstance();
    }

    @Override
    public boolean waitForFinalization(OstWorkflowContext.WORKFLOW_TYPE workflowType) {
        return true;
    }
}