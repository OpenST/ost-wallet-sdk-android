package com.ost.walletsdk.ui;

import com.ost.walletsdk.ui.loader.OstLoaderDelegate;
import com.ost.walletsdk.ui.loader.OstSdkLoaderManager;

public class OstResourceProvider {

    private static OstLoaderDelegate mBaseWorkflowLoader = null;

    public static void setApplicationLoaderManager(OstLoaderDelegate ostLoaderDelegate) {
        mBaseWorkflowLoader = ostLoaderDelegate;
    }

    public static OstLoaderDelegate getLoaderManager() {
        return null == mBaseWorkflowLoader ? new OstSdkLoaderManager() : mBaseWorkflowLoader;
    }
}