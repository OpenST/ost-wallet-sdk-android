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
