package com.ost.walletsdk.workflows.interfaces;

import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;

public interface OstTransactionWorkflowCallback extends OstWorkFlowCallback {
    void transactionMined(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);
}
