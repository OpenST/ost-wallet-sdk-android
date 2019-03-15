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

public class SignedRecoverOperationStruct extends BaseRecoveryOperationStruct {
    public String getPrevOwnerOfDeviceToRecover() {
        return prevOwnerOfDeviceToRecover;
    }

    public String getDeviceToRevoke() {
        return deviceToRevoke;
    }

    public String getDeviceToAuthorize() {
        return deviceToAuthorize;
    }

    private String prevOwnerOfDeviceToRecover;
    private String deviceToRevoke;
    private String deviceToAuthorize;

    public SignedRecoverOperationStruct(String primaryType, String prevOwnerOfDeviceToRecover, String deviceToRevoke, String deviceToAuthorize) {
        this.prevOwnerOfDeviceToRecover = prevOwnerOfDeviceToRecover;
        this.deviceToRevoke = deviceToRevoke;
        this.deviceToAuthorize = deviceToAuthorize;
        setPrimaryType(primaryType);
    }

}
