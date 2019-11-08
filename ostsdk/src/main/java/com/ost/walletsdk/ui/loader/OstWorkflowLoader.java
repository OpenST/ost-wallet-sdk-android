package com.ost.walletsdk.ui.loader;

import com.ost.walletsdk.ui.workflow.WorkflowCompleteDelegate;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

public interface OstWorkflowLoader {
    void onInitLoader();

    void onPostAuthentication();

    void onAcknowledge();

    void onSuccess(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, WorkflowCompleteDelegate delegate);

    void onFailure(OstWorkflowContext ostWorkflowContext, OstError ostError, WorkflowCompleteDelegate delegate);
}
