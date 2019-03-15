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

public class BaseDeviceManagerOperationStruct extends SignedEIP712Struct {

    public BaseDeviceManagerOperationStruct() {

    }

    public String getDeviceManagerAddress() {
        return getVerifyingContract();
    }

    public void setDeviceManagerAddress(String deviceManagerAddress) {
        setVerifyingContract(deviceManagerAddress);
    }

    public String getDeviceOwnerAddress() {
        return getSignerAddress();
    }

    public void setDeviceOwnerAddress(String deviceOwnerAddress) {
        setSignerAddress(deviceOwnerAddress);
    }

    private String callData;
    private String rawCallData;

    public String getCallData() {
        return callData;
    }
    public void setCallData(String callData) {
        this.callData = callData;
    }

    public void setRawCallData(String rawCallData) {
        this.rawCallData = rawCallData;
    }

    public String getRawCallData() {
        return rawCallData;
    }
}
