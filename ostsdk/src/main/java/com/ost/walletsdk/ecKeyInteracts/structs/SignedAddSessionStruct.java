/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ecKeyInteracts.structs;

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
