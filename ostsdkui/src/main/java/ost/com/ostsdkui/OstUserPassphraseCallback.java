package ost.com.ostsdkui;

public interface OstUserPassphraseCallback {
    void getPassphrase(String userId, OstPassphraseAcceptor ostPassphraseAcceptor);
}