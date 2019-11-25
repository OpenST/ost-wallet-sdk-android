package com.ost.walletsdk.network.polling.interfaces;

import com.ost.walletsdk.annotations.Nullable;

import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONObject;

public interface OstPollingCallback {
    void onOstPollingSuccess(@Nullable OstBaseEntity entity, @Nullable JSONObject data);
    void onOstPollingFailed(OstError error);
}
