package com.ost.mobilesdk.ecKeyInteracts.structs;

public class SignedRevokeDeviceStruct extends BaseDeviceManagerOperationStruct {
    private String deviceToBeRevoked;

    public SignedRevokeDeviceStruct(String deviceTobeRevoked) {
        this.deviceToBeRevoked = deviceTobeRevoked;
    }

    public String getDeviceToBeRevoked() {
        return toSafeCheckSumAddress(deviceToBeRevoked);
    }

    public void setDeviceToBeRevoked(String deviceToBeRevoked) {
        this.deviceToBeRevoked = deviceToBeRevoked;
    }
}