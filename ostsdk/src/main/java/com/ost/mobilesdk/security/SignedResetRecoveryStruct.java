package com.ost.mobilesdk.security;

import org.json.JSONObject;

public class SignedResetRecoveryStruct {

    private String recoveryContractAddress;
    private String signature;
    private String newRecoverOwnerAddress;
    private String recoveryOwnerAddress;
    private JSONObject eip712TypedData;

    public SignedResetRecoveryStruct(String newRecoverOwnerAddress, String recoveryOwnerAddress, String recoveryContractAddress, JSONObject typedData, String signature) {
        this.newRecoverOwnerAddress = newRecoverOwnerAddress;
        this.recoveryOwnerAddress = recoveryOwnerAddress;
        this.recoveryContractAddress = recoveryContractAddress;
        this.eip712TypedData = typedData;
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public String getNewRecoverOwnerAddress() {
        return newRecoverOwnerAddress;
    }

    public String getRecoveryOwnerAddress() {
        return recoveryOwnerAddress;
    }

    public String getRecoveryContractAddress() {
        return recoveryContractAddress;
    }

    public JSONObject getEip712TypedData() {
        return eip712TypedData;
    }

}
