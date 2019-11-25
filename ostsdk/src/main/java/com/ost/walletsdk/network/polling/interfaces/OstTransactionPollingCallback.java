package com.ost.walletsdk.network.polling.interfaces;

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.models.entities.OstTransaction;

public interface OstTransactionPollingCallback extends OstPollingCallback {
    void onTransactionMined(@NonNull OstTransaction transaction);
}
