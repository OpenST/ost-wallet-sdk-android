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
