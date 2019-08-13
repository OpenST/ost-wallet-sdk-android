package com.ost.walletsdk.ui.interfaces;

import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;

public interface RequestAcknowledgedListener extends OstWalletUIListener {
    void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);
}
