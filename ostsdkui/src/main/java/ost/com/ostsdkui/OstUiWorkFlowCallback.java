package ost.com.ostsdkui;

import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

public interface OstUiWorkFlowCallback {

    void onUiFlowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);

    void onUiFlowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError);
}
