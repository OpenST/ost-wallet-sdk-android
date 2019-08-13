package com.ost.walletsdk.ui.interfaces;

import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;

public interface FlowCompleteListener extends OstWalletUIListener {
    void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);
}
