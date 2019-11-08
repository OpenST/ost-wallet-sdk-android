package com.ost.walletsdk.ui;

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.ui.loader.OstLoaderDelegate;
import com.ost.walletsdk.ui.loader.OstSdkWorkflowLoader;

public class OstLoaderProvider {

    private static OstLoaderDelegate mBaseWorkflowLoader = new OstSdkWorkflowLoader();

    public static OstLoaderDelegate getBaseWorkflowLoader() {
        return mBaseWorkflowLoader;
    }

    public static void setLoaderManager(@NonNull OstLoaderDelegate ostWorkflowLoader) {
        mBaseWorkflowLoader = ostWorkflowLoader;
    }
}