package com.ost.mobilesdk.workflows.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OstUserPollingService extends OstPollingService {

    private static final String TAG = "OstUserPollingService";

    public OstUserPollingService() {
        super();
    }

    /**
     * Starts this service to perform polling with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startPolling(String userId, String entityId, String fromStatus, String toStatus) {
        Context context = OstSdk.getContext();
        Intent intent = new Intent(context, OstUserPollingService.class);
        OstPollingService.startPolling(context, intent, userId, entityId, fromStatus, toStatus);
    }

    @Override
    protected OstBaseEntity parseEntity(JSONObject entityObject) throws JSONException {
        return OstUser.parse(entityObject);
    }

    @Override
    protected Intent getServiceIntent(Context context) {
        return new Intent(context, OstUserPollingService.class);
    }

    @Override
    protected String getEntityName() {
        return OstSdk.USER;
    }

    @Override
    protected JSONObject poll(String userId, String entityId) throws IOException {
        return new OstApiClient(userId).getUser();
    }

    @Override
    protected boolean validateParams(String entityId, String userId, String entityName) {
        //Todo:: check status
        return false;
    }
}