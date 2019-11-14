package com.ost.walletsdk.ui;

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.ui.loader.OstLoaderDelegate;
import com.ost.walletsdk.ui.loader.OstSdkLoaderManager;

public class OstLoaderProvider {

    private static OstLoaderDelegate mBaseWorkflowLoader = new OstSdkLoaderManager();

    public static OstLoaderDelegate getBaseWorkflowLoader() {
        return mBaseWorkflowLoader;
    }

    public static void setLoaderManager(@NonNull OstLoaderDelegate ostWorkflowLoader) {
        mBaseWorkflowLoader = ostWorkflowLoader;
    }
}