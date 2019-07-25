package ost.com.ostsdkui;

import com.ost.walletsdk.workflows.interfaces.OstBaseInterface;

public interface OstPassphraseAcceptor extends OstBaseInterface {
    void setPassphrase(String passphrase);
}