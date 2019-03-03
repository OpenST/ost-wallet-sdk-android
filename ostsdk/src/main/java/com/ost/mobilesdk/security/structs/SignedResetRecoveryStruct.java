package com.ost.mobilesdk.security.structs;

public class SignedResetRecoveryStruct extends BaseRecoveryOperationStruct {
    public SignedResetRecoveryStruct(String newRecoveryOwnerAddress) {
        this.newRecoveryOwnerAddress = newRecoveryOwnerAddress;
    }

    public String getNewRecoveryOwnerAddress() {
        return newRecoveryOwnerAddress;
    }

    private String newRecoveryOwnerAddress;
}
