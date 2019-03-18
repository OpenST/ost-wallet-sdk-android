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
import com.ost.walletsdk.models.entities.OstTransaction;
import com.ost.walletsdk.network.OstApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OstTransactionPollingService extends OstPollingService {

    private static final String TAG = "OstTransactionPollingService";

    public OstTransactionPollingService(String userId, String entityId, String successStatus, String failureStatus) {
        super(userId, entityId, successStatus, failureStatus);
    }

    /**
     * Starts this service to perform polling with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static Bundle startPolling(String userId, String entityId, String successStatus, String failureStatus) {
        OstTransactionPollingService ostTransactionPollingService = new OstTransactionPollingService(userId, entityId, successStatus, failureStatus);
        return ostTransactionPollingService.waitForUpdate();
    }

    @Override
    protected OstBaseEntity parseEntity(JSONObject entityObject) throws JSONException {
        return OstTransaction.parse(entityObject);
    }

    @Override
    protected Intent getServiceIntent(Context context) {
        return new Intent(context, OstTransactionPollingService.class);
    }

    @Override
    protected String getEntityName() {
        return OstSdk.TRANSACTION;
    }

    @Override
    protected JSONObject poll(String transactionId, String entityId) throws IOException {
        return new OstApiClient(transactionId).getTransaction(entityId);
    }

    @Override
    protected boolean validateParams(String entityId, String successStatus, String failureStatus) {
        return OstTransaction.isValidStatus(successStatus) && OstTransaction.isValidStatus(failureStatus);
    }
}