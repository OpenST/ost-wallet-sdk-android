package com.ost.mobilesdk.workflows.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstRecoveryOwner;
import com.ost.mobilesdk.network.OstApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OstRecoveryPollingService extends OstPollingService {

    private static final String TAG = "OstRecoveryPollingService";

    public OstRecoveryPollingService(String userId, String entityId, String successStatus, String failedStatus) {
        super(userId, entityId, successStatus, failedStatus);
    }

    /**
     * Starts this service to perform polling with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static Bundle startPolling(String userId, String entityId, String successStatus, String failedStatus) {
        OstRecoveryPollingService ostRecoveryPollingService = new OstRecoveryPollingService(userId, entityId, successStatus, failedStatus);
        return ostRecoveryPollingService.waitForUpdate();
    }

    @Override
    protected OstBaseEntity parseEntity(JSONObject entityObject) throws JSONException {
        return OstRecoveryOwner.parse(entityObject);
    }

    @Override
    protected Intent getServiceIntent(Context context) {
        return new Intent(context, OstRecoveryPollingService.class);
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