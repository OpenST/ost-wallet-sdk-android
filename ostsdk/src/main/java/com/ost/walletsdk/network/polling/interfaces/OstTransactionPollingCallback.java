package com.ost.walletsdk.network.polling.interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ost.walletsdk.models.entities.OstTransaction;

import org.json.JSONObject;

public interface OstTransactionPollingCallback extends OstPollingCallback {
    void onTransactionMined(@NonNull OstTransaction transaction);
}
