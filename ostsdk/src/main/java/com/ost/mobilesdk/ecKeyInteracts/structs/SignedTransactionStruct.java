package com.ost.mobilesdk.ecKeyInteracts.structs;

import com.ost.mobilesdk.models.entities.OstSession;

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