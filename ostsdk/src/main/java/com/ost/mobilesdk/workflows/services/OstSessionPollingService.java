package com.ost.mobilesdk.workflows.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstSession;
import com.ost.mobilesdk.network.OstApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OstSessionPollingService extends OstPollingService {

    private static final String TAG = "OstSessionPollingService";

    public OstSessionPollingService() {
        super();
    }

    /**
     * Starts this service to perform polling with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startPolling(String sessionId, String entityId, String fromStatus, String toStatus) {
        Context context = OstSdk.getContext();
        Intent intent = new Intent(context, OstSessionPollingService.class);
        OstPollingService.startPolling(context, intent, sessionId, entityId, fromStatus, toStatus);
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
        return OstSdk.DEVICE;
    }

    @Override
    protected JSONObject poll(String sessionId, String entityId) throws IOException {
        return new OstApiClient(sessionId).getSession(entityId);
    }

    @Override
    protected boolean validateParams(String entityId, String fromStatus, String toStatus) {
        return null != OstSession.getById(entityId) && OstSession.isValidStatus(fromStatus) && OstSession.isValidStatus(toStatus);
    }
}