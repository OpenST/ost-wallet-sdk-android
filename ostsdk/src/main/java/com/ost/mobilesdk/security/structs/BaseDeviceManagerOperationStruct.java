package com.ost.mobilesdk.security.structs;

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

    public String getExecutableData() {
        return executableData;
    }

    public void setExecutableData(String executableData) {
        this.executableData = executableData;
    }

    private String executableData;
}
