package com.ost.walletsdk.ui.interfaces;

import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;

public interface FlowCompleteListener extends OstWalletUIListener {
    /**
     * Inform SDK user the the flow is complete.
     * @param ostWorkflowContext A context that describes the workflow for which the callback was triggered with workflow id.
     * @param ostContextEntity Context Entity
     */
    void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);
}
