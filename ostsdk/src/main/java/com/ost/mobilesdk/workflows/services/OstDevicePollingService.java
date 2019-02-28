package com.ost.mobilesdk.workflows.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.network.OstApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OstDevicePollingService extends OstPollingService {

    private static final String TAG = "OstDevicePollingService";

    public OstDevicePollingService() {
        super();
    }

    /**
     * Starts this service to perform polling with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startPolling(String userId, String entityId, String successStatus, String failedStatus) {
        Context context = OstSdk.getContext();
        Intent intent = new Intent(context, OstDevicePollingService.class);
        OstPollingService.startPolling(context, intent, userId, entityId, successStatus, failedStatus);
    }

    @Override
    protected OstBaseEntity parseEntity(JSONObject entityObject) throws JSONException {
        return OstDevice.parse(entityObject);
    }

    @Override
    protected Intent getServiceIntent(Context context) {
        return new Intent(context, OstDevicePollingService.class);
    }

    @Override
    protected String getEntityName() {
        return OstSdk.DEVICE;
    }

    @Override
    protected JSONObject poll(String deviceId, String entityId) throws IOException {
        return new OstApiClient(deviceId).getDevices(entityId);
    }

    @Override
    protected boolean validateParams(String entityId, String successStatus, String failedStatus) {
        return null != OstDevice.getById(entityId) && OstDevice.isValidStatus(successStatus) && OstDevice.isValidStatus(failedStatus);
    }
}