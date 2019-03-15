/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.ecKeyInteracts.structs;

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
