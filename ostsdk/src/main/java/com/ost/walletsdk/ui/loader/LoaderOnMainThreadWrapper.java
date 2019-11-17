package com.ost.walletsdk.ui.loader;

import android.os.Handler;
import android.os.Looper;

import com.ost.walletsdk.ui.workflow.OstLoaderCompletionDelegate;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONObject;


public class LoaderOnMainThreadWrapper implements OstWorkflowLoader {

    private final OstWorkflowLoader mLoader;

    public LoaderOnMainThreadWrapper(OstWorkflowLoader loaderFragment) {
        mLoader = loaderFragment;
    }

    @Override
    public void onInitLoader(JSONObject contentConfig) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mLoader.onInitLoader(contentConfig);
            }
        });
    }

    @Override
    public void onPostAuthentication(JSONObject contentConfig) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mLoader.onPostAuthentication(contentConfig);
            }
        });
    }

    @Override
    public void onAcknowledge(JSONObject contentConfig) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mLoader.onAcknowledge(contentConfig);
            }
        });
    }

    @Override
    public void onSuccess(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstLoaderCompletionDelegate delegate) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mLoader.onSuccess(ostWorkflowContext, ostContextEntity, delegate);
            }
        });
    }

    @Override
    public void onFailure(OstWorkflowContext ostWorkflowContext, OstError ostError, OstLoaderCompletionDelegate delegate) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mLoader.onFailure(ostWorkflowContext, ostError, delegate);
            }
        });
    }
}