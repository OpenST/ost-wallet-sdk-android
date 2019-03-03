package com.ost.mobilesdk.security.structs;

public class BaseRecoveryOperationStruct extends SignedEIP712Struct {

    private String callData;

    public String getRecoveryOwnerAddress() {
        return getSignerAddress();
    }

    public void setRecoveryOwnerAddress(String recoveryOwnerAddress) {
        setSignerAddress(recoveryOwnerAddress);
    }

    public String getRecoveryContractAddress() {
        return getVerifyingContract();
    }

    public void setRecoveryContractAddress(String recoveryContractAddress) {
        setVerifyingContract(recoveryContractAddress);
    }

    public String getCallData() {
        return callData;
    }
    public void setCallData(String callData) {
        this.callData = callData;
    }

}
