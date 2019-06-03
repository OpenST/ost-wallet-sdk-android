package com.ost.walletsdk.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONObject;

public interface OstJsonApiCallback {
    void onOstJsonApiSuccess(@Nullable JSONObject data);
    void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject data);
}
