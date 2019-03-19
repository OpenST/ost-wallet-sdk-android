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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.models.entities.OstRecoveryOwner;
import com.ost.walletsdk.network.OstApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OstRecoveryPollingService extends OstPollingService {

    private static final String TAG = "OstRecoveryPollingService";

    private OstRecoveryPollingService(String userId, String entityId, String successStatus, String failedStatus) {
        super(userId, entityId, successStatus, failedStatus);
    }

    public static Bundle startPolling(String userId, String entityId, String successStatus, String failedStatus) {
        OstRecoveryPollingService ostRecoveryPollingService = new OstRecoveryPollingService(userId, entityId, successStatus, failedStatus);
        return ostRecoveryPollingService.waitForUpdate();
    }

    @Override
    protected OstBaseEntity parseEntity(JSONObject entityObject) throws JSONException {
        return OstRecoveryOwner.parse(entityObject);
    }

    @Override
    protected String getEntityName() {
        return OstSdk.RECOVERY_OWNER;
    }

    @Override
    protected JSONObject poll(String userId, String entityId) throws IOException {
        return new OstApiClient(userId).getRecoveryOwnerAddress(entityId);
    }

    @Override
    protected boolean validateParams(String entityId, String successStatus, String failedStatus) {
        return OstRecoveryOwner.isValidStatus(successStatus) && OstRecoveryOwner.isValidStatus(failedStatus);
    }
}