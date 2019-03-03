package com.ost.mobilesdk.security.structs;

public class OstSignWithMnemonicsStruct {

    public byte[] getMnemonics() {
        return mnemonics;
    }

    public void setMnemonics(byte[] mnemonics) {
        this.mnemonics = mnemonics;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public void setMessageHash(String messageHash) {
        this.messageHash = messageHash;
    }

    private byte[] mnemonics;
    private String messageHash;

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    private String signer;
    private String signature;

    public OstSignWithMnemonicsStruct(byte[] mnemonics, String messageHash) {
        this.mnemonics = mnemonics;
        this.messageHash = messageHash;
    }
}
