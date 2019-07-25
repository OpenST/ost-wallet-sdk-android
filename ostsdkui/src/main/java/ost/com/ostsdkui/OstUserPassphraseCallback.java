package ost.com.ostsdkui;

import com.ost.walletsdk.workflows.OstWorkflowContext;

public interface OstUserPassphraseCallback {
    void getPassphrase(String userId, OstWorkflowContext ostWorkflowContext, OstPassphraseAcceptor ostPassphraseAcceptor);
}