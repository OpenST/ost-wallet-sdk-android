package com.ost.walletsdk.ui.loader;

import com.ost.walletsdk.ui.workflow.OstLoaderCompletionDelegate;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONObject;

public interface OstWorkflowLoader {
    void onInitLoader(JSONObject contentConfig);

    void onPostAuthentication(JSONObject contentConfig);

    void onAcknowledge(JSONObject contentConfig);

    void onSuccess(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, JSONObject contentConfig ,OstLoaderCompletionDelegate delegate);

    void onFailure(OstWorkflowContext ostWorkflowContext, OstError ostError, JSONObject contentConfig ,OstLoaderCompletionDelegate delegate);
}
