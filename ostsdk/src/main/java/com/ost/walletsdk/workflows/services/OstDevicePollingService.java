/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.workflows.services;

import android.os.Bundle;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.network.OstApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OstDevicePollingService extends OstPollingService {

    private static final String TAG = "OstDevicePollingService";

    private OstDevicePollingService(String userId, String entityId, String successStatus, String failedStatus) {
        super(userId, entityId, successStatus, failedStatus);
    }

    public static Bundle startPolling(String userId, String entityId, String successStatus, String failedStatus) {
        OstDevicePollingService ostDevicePollingService = new OstDevicePollingService(userId, entityId, successStatus, failedStatus);
        return ostDevicePollingService.waitForUpdate();
    }

    @Override
    protected OstBaseEntity parseEntity(JSONObject entityObject) throws JSONException {
        return OstDevice.parse(entityObject);
    }

    @Override
    protected String getEntityName() {
        return OstSdk.DEVICE;
    }

    @Override
    protected JSONObject poll(String deviceId, String entityId) throws IOException {
        return new OstApiClient(deviceId).getDevice(entityId);
    }

    @Override
    protected boolean validateParams(String entityId, String successStatus, String failedStatus) {
        return null != OstDevice.getById(entityId) && OstDevice.isValidStatus(successStatus) && OstDevice.isValidStatus(failedStatus);
    }
}