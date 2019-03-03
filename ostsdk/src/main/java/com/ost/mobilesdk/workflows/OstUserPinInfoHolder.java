package com.ost.mobilesdk.workflows;

public class OstUserPinInfoHolder {
    public String getPassphrasePrefix() {
        return passphrasePrefix;
    }

    public void setPassphrasePrefix(String passphrasePrefix) {
        this.passphrasePrefix = passphrasePrefix;
    }

    public String getUserPassphrase() {
        return userPassphrase;
    }

    public void setUserPassphrase(String userPassphrase) {
        this.userPassphrase = userPassphrase;
    }

    public String getScriptSalt() {
        return scriptSalt;
    }

    void setScriptSalt(String scriptSalt) {
        this.scriptSalt = scriptSalt;
    }

    private String passphrasePrefix;
    private String userPassphrase;
    private String scriptSalt;
    public OstUserPinInfoHolder(String passphrasePrefix, String userPassphrase) {
        this.passphrasePrefix = passphrasePrefix;
        this.userPassphrase = userPassphrase;
    }

    public OstUserPinInfoHolder() {

    }

}
