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
import android.util.Log;

import com.ost.walletsdk.OstConfigs;
import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstBaseEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public abstract class OstPollingService {

    public static final String EXTRA_USER_ID = "com.ost.mobilesdk.workflows.extra.USER_ID";
    public static final String EXTRA_ENTITY_ID = "com.ost.mobilesdk.workflows.extra.ENTITY_ID";
    public static final String EXTRA_ENTITY_FAILURE_STATUS = "com.ost.mobilesdk.workflows.extra.ENTITY_FAILURE_STATUS";
    public static final String EXTRA_ENTITY_SUCCESS_STATUS = "com.ost.mobilesdk.workflows.extra.ENTITY_SUCCESS_STATUS";
    public static final String EXTRA_POLL_COUNT = "com.ost.mobilesdk.workflows.extra.POLL_COUNT";
    public static final String ENTITY_UPDATE_MESSAGE = "com.ost.mobilesdk.workflows.extra.ENTITY_UPDATE";
    public static final String EXTRA_ENTITY_TYPE = "com.ost.mobilesdk.workflows.extra.ENTITY_TYPE";
    public static final String EXTRA_IS_POLLING_TIMEOUT = "com.ost.mobilesdk.workflows.extra.IS_POLLING_TIMEOUT";
    public static final String EXTRA_IS_VALID_RESPONSE = "com.ost.mobilesdk.workflows.extra.IS_VALID_RESPONSE";

    private static final int POLL_MAX_COUNT = 10;

    private static final String TAG = "OstPollingService";
    private static final long POLLING_INTERVAL = OstConfigs.getInstance().BLOCK_GENERATION_TIME * 1000;
    private static final long INITIAL_POLLING_INTERVAL = 6 * POLLING_INTERVAL;
    private final String failedStatus;
    private final String successStatus;
    private final String entityId;
    private final String userId;
    private int pollCount;

    public OstPollingService(String userId, String entityId, String successStatus, String failedStatus) {
        this.userId = userId;
        this.entityId = entityId;
        this.successStatus = successStatus;
        this.failedStatus = failedStatus;
        this.pollCount = POLL_MAX_COUNT;
    }

    synchronized public Bundle waitForUpdate() {
        if (!isValidUserId(userId)) {
            Log.e(TAG, String.format("Invalid User Id: %s", userId));
            return new Bundle();
        }

        if (!validateParams(entityId, successStatus, failedStatus)) {
            Log.e(TAG, String.format("Invalid Entity Params for Entity: %s, EntityId: %s, " +
                            "From Status: %s, To Status: %s", getEntityName(), entityId,
                    successStatus, failedStatus));
            return new Bundle();
        }

        try {
            wait(INITIAL_POLLING_INTERVAL);
        } catch (InterruptedException e) {
            Log.e(TAG, String.format("Initial polling interval wait interrupted for %s entity Id: %s", getEntityName(), entityId), e);
        }
        return startPolling();
    }

    synchronized protected Bundle startPolling() {
        while (pollCount > 0) {
            Log.i(TAG, String.format("Polling... of entity %s", getEntityName()));
            JSONObject response = null;
            try {
                response = poll(userId, entityId);
            } catch (IOException e) {
                Log.e(TAG, String.format("IOException: %s", e.getCause()));
            }

            Log.d(TAG, String.format("Response of %s poll is %s", getEntityName(), response.toString()));
            Log.i(TAG, String.format("Checking response validity of %s entity", getEntityName()));
            boolean isValidResponse = isResponseValid(response);
            Log.d(TAG, String.format("Response of %s entity validity: %b", getEntityName(), isValidResponse));
            if (!isValidResponse) {
                return sendUpdateMessage(userId, entityId, false, false);
            }

            Log.i(TAG, String.format("Checking %s entity update status", getEntityName()));
            String status = updatedGivenStatus(response, successStatus, failedStatus);
            if (null != status) {
                Log.d(TAG, String.format("Is %s entity updated status %s", getEntityName(), status));
                return sendUpdateMessage(userId, entityId, false, status.equalsIgnoreCase(successStatus));
            }
            pollCount = pollCount - 1;
            try {
                wait(POLLING_INTERVAL);
            } catch (InterruptedException e) {
                Log.e(TAG, String.format("Initial polling interval wait interrupted for %s entity Id: %s", getEntityName(), entityId), e);
            }
        }

        Log.d(TAG, String.format("Poll count reach to zero for %s", getEntityName()));
        return sendUpdateMessage(userId, entityId, true);
    }

    private Bundle sendUpdateMessage(String userId, String entityId, boolean pollingTimeout) {
        return sendUpdateMessage(userId, entityId, pollingTimeout, true);
    }

    private Bundle sendUpdateMessage(String userId, String entityId, boolean pollingTimeout, boolean validResponse) {
        Bundle bundle = new Bundle();
        // You can also include some extra data.
        bundle.putString(EXTRA_USER_ID, userId);
        bundle.putString(EXTRA_ENTITY_ID, entityId);
        bundle.putString(EXTRA_ENTITY_TYPE, getEntityName());
        bundle.putBoolean(EXTRA_IS_POLLING_TIMEOUT, pollingTimeout);
        bundle.putBoolean(EXTRA_IS_VALID_RESPONSE, validResponse);

        return bundle;
    }

    private boolean isValidUserId(String userId) {
        return null != OstSdk.getUser(userId);
    }

    private String updatedGivenStatus(JSONObject response, String entitySuccessStatus, String entityFailureStatus) {
        JSONObject jsonData = response.optJSONObject(OstConstants.RESPONSE_DATA);
        JSONObject entityObject = jsonData.optJSONObject(getEntityName());
        String currentStatus;
        try {
            OstBaseEntity ostBaseEntity = parseEntity(entityObject);
            currentStatus = ostBaseEntity.getStatus();
        } catch (JSONException e) {
            Log.d(TAG, "JSONException", e);
            return null;
        }
        Log.d(TAG, String.format("Entity Success status: %s, Entity Failure status %s, Entity Current status %s",
                entitySuccessStatus, entityFailureStatus, currentStatus));

        if (entitySuccessStatus.equalsIgnoreCase(currentStatus)) {
            return entitySuccessStatus;
        }
        if (entityFailureStatus.equalsIgnoreCase(currentStatus)) {
            return entityFailureStatus;
        }
        Log.d(TAG, String.format("No update received for %s entity", getEntityName()));
        return null;
    }

    protected abstract OstBaseEntity parseEntity(JSONObject entityObject) throws JSONException;

    private boolean isResponseValid(JSONObject response) {
        try {
            if (!response.getBoolean(OstConstants.RESPONSE_SUCCESS)) {
                return false;
            }
            JSONObject jsonData = response.getJSONObject(OstConstants.RESPONSE_DATA);
            jsonData.has(getEntityName());
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected abstract Intent getServiceIntent(Context context);

    protected abstract String getEntityName();

    protected abstract JSONObject poll(String userId, String entityId) throws IOException;

    protected abstract boolean validateParams(String entityId, String fromStatus, String toStatus);
}