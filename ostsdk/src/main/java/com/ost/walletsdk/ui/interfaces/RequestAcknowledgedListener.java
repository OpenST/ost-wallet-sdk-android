package com.ost.walletsdk.ui.interfaces;

import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;

public interface RequestAcknowledgedListener extends OstWalletUIListener {
    /**
     * Acknowledge user about the request which is going to make by SDK.
     * @param ostWorkflowContext A context that describes the workflow for which the callback was triggered with workflow id.
     * @param ostContextEntity Context Entity
     */
    void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);
}
