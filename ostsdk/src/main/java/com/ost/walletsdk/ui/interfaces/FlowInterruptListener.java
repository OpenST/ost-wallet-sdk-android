package com.ost.walletsdk.ui.interfaces;

import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

public interface FlowInterruptListener extends OstWalletUIListener {
    /**
     * Inform SDK user that flow is interrupted with errorCode.
     * Developers should dismiss pin dialog (if open) on this callback.
     * @param ostWorkflowContext A context that describes the workflow for which the callback was triggered with workflow id.
     * @param ostError Error Entity
     */
    void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError);
}
