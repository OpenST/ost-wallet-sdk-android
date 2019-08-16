package com.ost.walletsdk.ui;

import com.ost.walletsdk.workflows.OstWorkflowContext;

public interface OstUserPassphraseCallback {
    /**
     * Get passphrase prefix from application
     * @param userId Ost user id
     * @param ostWorkflowContext Workflow context
     * @param ostPassphraseAcceptor Passphrase prefix accept callback
     */
    void getPassphrase(String userId, OstWorkflowContext ostWorkflowContext, OstPassphraseAcceptor ostPassphraseAcceptor);
}