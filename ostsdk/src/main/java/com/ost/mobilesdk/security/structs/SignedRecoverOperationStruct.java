package com.ost.mobilesdk.security.structs;

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
