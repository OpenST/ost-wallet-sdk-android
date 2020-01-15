package com.ost.walletsdk.network;

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;

import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONObject;

public interface OstJsonApiCallback {
    void onOstJsonApiSuccess(@Nullable JSONObject data);
    void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject response);
}
