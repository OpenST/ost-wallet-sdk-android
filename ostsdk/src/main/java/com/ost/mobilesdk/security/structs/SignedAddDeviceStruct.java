package com.ost.mobilesdk.security.structs;

public class SignedAddDeviceStruct extends BaseDeviceManagerOperationStruct {

    private String deviceToBeAdded;

    public SignedAddDeviceStruct(String deviceToBeAdded) {
        this.deviceToBeAdded = deviceToBeAdded;
    }

    public String getDeviceToBeAdded() {
        return toSafeCheckSumAddress(deviceToBeAdded);
    }

    public void setDeviceToBeAdded(String deviceToBeAdded) {
        this.deviceToBeAdded = deviceToBeAdded;
    }


}
