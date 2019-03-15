/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.ecKeyInteracts;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.HashMap;

public class KeyMetaStruct implements Serializable {
    static final long serialVersionUID = 129348938L;
    String apiAddress;
    String deviceAddress;
    HashMap<String, String> ethKeyMetaMapping = new HashMap<>();
    HashMap<String, String> ethKeyMnemonicsMetaMapping = new HashMap<>();

    KeyMetaStruct() {
        this.apiAddress = "";
        this.deviceAddress = "";
    }

    String getApiAddress() {
        return apiAddress;
    }

    String getEthKeyIdentifier(String address) {
        return ethKeyMetaMapping.get(address);
    }

    boolean hasAddress(String address) {
        return !TextUtils.isEmpty( getEthKeyIdentifier(address) );
    }

    String getEthKeyMnemonicsIdentifier(String address) {
        return ethKeyMnemonicsMetaMapping.get(address);
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }
}