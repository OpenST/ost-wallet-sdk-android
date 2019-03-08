package com.ost.mobilesdk.workflows.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstSession;
import com.ost.mobilesdk.network.OstApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OstSessionPollingService extends OstPollingService {

    private static final String TAG = "OstSessionPollingService";

    public OstSessionPollingService(String userId, String entityId, String successStatus, String failureStatus) {
        super(userId, entityId, successStatus, failureStatus);
    }

    /**
     * Starts this service to perform polling with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static Bundle startPolling(String userId, String entityId, String successStatus, String failureStatus) {
        OstSessionPollingService ostSessionPollingService = new OstSessionPollingService(userId, entityId, successStatus, failureStatus);
        return ostSessionPollingService.waitForUpdate();
    }

    @Override
    protected OstBaseEntity parseEntity(JSONObject entityObject) throws JSONException {
        return OstSession.parse(entityObject);
    }

    @Override
    protected Intent getServiceIntent(Context context) {
        return new Intent(context, OstSessionPollingService.class);
    }

    @Override
    protected String getEntityName() {
        return OstSdk.SESSION;
    }

    @Override
    protected JSONObject poll(String sessionId, String entityId) throws IOException {
        return new OstApiClient(sessionId).getSession(entityId);
    }

    @Override
    protected boolean validateParams(String entityId, String successStatus, String failureStatus) {
        return OstSession.isValidStatus(successStatus) && OstSession.isValidStatus(failureStatus);
    }
}