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

import org.json.JSONObject;
import org.web3j.crypto.Keys;

public class SignedEIP712Struct {
    public String getSignerAddress() {
        return Keys.toChecksumAddress(signerAddress);
    }

    public void setSignerAddress(String signerAddress) {
        this.signerAddress = signerAddress;
    }

    public String getVerifyingContract() {
        return toSafeCheckSumAddress(verifyingContract);
    }

    public void setVerifyingContract(String verifyingContract) {
        this.verifyingContract = verifyingContract;
    }

    public String getToAddress() {
        return toSafeCheckSumAddress(toAddress);
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public JSONObject getTypedData() {
        return typedData;
    }

    public void setTypedData(JSONObject typedData) {
        this.typedData = typedData;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public void setMessageHash(String messageHash) {
        this.messageHash = messageHash;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    private String signerAddress;
    private String verifyingContract;
    private String toAddress;
    private JSONObject typedData;
    private String messageHash;
    private String signature;
    private String nonce;

    public String getPrimaryType() {
        return primaryType;
    }

    void setPrimaryType(String primaryType) {
        this.primaryType = primaryType;
    }

    private String primaryType;


    public SignedEIP712Struct() {

    }

    public String toSafeCheckSumAddress(String address) {
        if ( null == address ) {
            return address;
        }
        return Keys.toChecksumAddress(address);
    }
}
