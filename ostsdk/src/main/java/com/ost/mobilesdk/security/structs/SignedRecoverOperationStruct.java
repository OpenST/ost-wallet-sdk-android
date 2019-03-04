package com.ost.mobilesdk.security.structs;

public class SignedRecoverOperationStruct extends BaseRecoveryOperationStruct {
    String prevOwnerOfDeviceToRecover;
    String deviceToRevoke;
    String deviceToAuthorize;

    public SignedRecoverOperationStruct(String primaryType, String prevOwnerOfDeviceToRecover, String deviceToRevoke, String deviceToAuthorize) {
        this.prevOwnerOfDeviceToRecover = prevOwnerOfDeviceToRecover;
        this.deviceToRevoke = deviceToRevoke;
        this.deviceToAuthorize = deviceToAuthorize;
        setPrimaryType(primaryType);
    }

}
