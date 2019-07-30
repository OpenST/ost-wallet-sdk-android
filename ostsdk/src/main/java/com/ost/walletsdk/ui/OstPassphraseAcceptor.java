package com.ost.walletsdk.ui;

import com.ost.walletsdk.workflows.interfaces.OstBaseInterface;

public interface OstPassphraseAcceptor extends OstBaseInterface {
    void setPassphrase(String passphrase);
}