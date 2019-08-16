/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.managedevices;

import com.ost.walletsdk.models.entities.OstDevice;

import org.json.JSONObject;

public class Device {
    private static final String LOG_TAG = "OstAppDevice";
    private static final String OST_USER_ID = "user_id";
    private static final String ADDRESS = "address";
    private static final String API_SIGNER_ADDRESS = "api_signer_address";
    private static final String STATUS = "status";


    public static Device newInstance(JSONObject jsonObject) {
        String userId = jsonObject.optString(OST_USER_ID);
        String address = jsonObject.optString(ADDRESS);
        String apiSignerAddress = jsonObject.optString(API_SIGNER_ADDRESS);
        String status = jsonObject.optString(STATUS);
        return new Device(userId, address, apiSignerAddress, status);
    }

    public String getOstUserId() {
        return ostUserId;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public String getApiSignerAddress() {
        return apiSignerAddress;
    }

    public String getStatus() {
        return status;
    }

    private String ostUserId;
    private String deviceAddress;
    private String apiSignerAddress;
    private String status;

    private Device(String ostUserId, String deviceAddress, String apiSignerAddress, String status) {
        this.ostUserId = ostUserId;
        this.deviceAddress = deviceAddress;
        this.apiSignerAddress = apiSignerAddress;
        this.status = status;
    }

    public boolean isAuthorized() {
        return (OstDevice.CONST_STATUS.AUTHORIZED.equalsIgnoreCase(status));
    }
}