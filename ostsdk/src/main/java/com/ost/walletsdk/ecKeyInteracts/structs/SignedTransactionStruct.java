/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ecKeyInteracts.structs;

import com.ost.walletsdk.models.entities.OstSession;

public class SignedTransactionStruct {

    final private OstSession activeSession;
    final private String signature;
    final private String rawCallData;
    final private String callData;
    final private String tokenHolderContractAddress;

    public SignedTransactionStruct(OstSession activeSession, String tokenHolderContractAddress,
                                   String rawCallData, String callData, String signature) {
        this.activeSession = activeSession;
        this.tokenHolderContractAddress = tokenHolderContractAddress;
        this.rawCallData = rawCallData;
        this.callData = callData;
        this.signature = signature;

    }

    public String getTokenHolderContractAddress() {
        return tokenHolderContractAddress;
    }

    public String getCallData() {
        return callData;
    }

    public String getNonce() {
        return String.valueOf(activeSession.getNonce());
    }

    public String getRawCallData() {
        return rawCallData;
    }

    public String getSignature() {
        return signature;
    }

    public String getSignerAddress() {
        return activeSession.getAddress();
    }

    public OstSession getSession() {
        return activeSession;
    }
}