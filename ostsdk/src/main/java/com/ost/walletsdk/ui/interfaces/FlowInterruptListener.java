package com.ost.walletsdk.ui.interfaces;

import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

public interface FlowInterruptListener extends OstWalletUIListener {
    void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError);
}
