package com.ost.walletsdk.ui.loader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.workflow.OstLoaderCompletionDelegate;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONObject;

public class OstLoaderFragment extends DialogFragment implements OstWorkflowLoader {

    private OstWorkflowContext.WORKFLOW_TYPE mWorkflowType;
    private String mLoaderString = "Loading...";

    private ProgressBar mProgressBar;
    private TextView mLoaderTextView;

    public OstLoaderFragment() {
    }

    public static OstLoaderFragment newInstance(OstWorkflowContext.WORKFLOW_TYPE workflowType) {
        OstLoaderFragment loaderFragment = new OstLoaderFragment();
        loaderFragment.mWorkflowType = workflowType;
        return loaderFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.loader_fragment, container, false);
        mProgressBar = viewGroup.findViewById(R.id.progressBar);
        mLoaderTextView = viewGroup.findViewById(R.id.loaderText);
        mLoaderTextView.setText(mLoaderString);
        mProgressBar.animate();
        return viewGroup;
    }

    public void setLoaderString(String loaderString) {
        mLoaderString = loaderString;
    }

    @Override
    public void onInitLoader(JSONObject contentConfig) {

    }

    @Override
    public void onPostAuthentication(JSONObject contentConfig) {

    }

    @Override
    public void onAcknowledge(JSONObject contentConfig) {

    }

    @Override
    public void onSuccess(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, JSONObject contentConfig, final OstLoaderCompletionDelegate delegate) {

    }

    @Override
    public void onFailure(OstWorkflowContext ostWorkflowContext, OstError ostError, JSONObject contentConfig, final OstLoaderCompletionDelegate delegate) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}