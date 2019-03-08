package com.ost.mobilesdk.ecKeyInteracts.structs;

public class SignedAddSessionStruct extends BaseDeviceManagerOperationStruct {
    private String sessionAddress;
    private String spendingLimit;
    private String expiryHeight;

    public SignedAddSessionStruct(String sessionAddress, String spendingLimit, String expiryHeight) {
        this.sessionAddress = sessionAddress;
        this.spendingLimit = spendingLimit;
        this.expiryHeight = expiryHeight;
    }

    public String getTokenHolderAddress() {
        return getToAddress();
    }

    public void setTokenHolderAddress(String tokenHolderAddress) {
        setToAddress(tokenHolderAddress);
    }

}
