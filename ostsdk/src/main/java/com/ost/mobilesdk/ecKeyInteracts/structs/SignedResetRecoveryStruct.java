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

public class SignedResetRecoveryStruct extends BaseRecoveryOperationStruct {
    public SignedResetRecoveryStruct(String newRecoveryOwnerAddress) {
        this.newRecoveryOwnerAddress = newRecoveryOwnerAddress;
        setPrimaryType("ResetRecoveryOwnerStruct");
    }

    public String getNewRecoveryOwnerAddress() {
        return newRecoveryOwnerAddress;
    }

    private String newRecoveryOwnerAddress;
}
