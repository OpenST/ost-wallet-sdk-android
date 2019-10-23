package com.ost.walletsdk.network.polling;

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;

import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.models.entities.OstTransaction;
import com.ost.walletsdk.models.entities.OstTransaction.TransactionStatus;
import com.ost.walletsdk.network.polling.interfaces.OstPollingCallback;
import com.ost.walletsdk.network.polling.interfaces.OstTransactionPollingCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class OstTransactionPollingHelper extends OstBasePollingHelper {
    private final String entityId;
    private boolean hasCalledMinedCallback = false;
    public OstTransactionPollingHelper(@NonNull String transactionId, @NonNull String userId, @NonNull OstTransactionPollingCallback callback) {
        super(userId, callback);
        this.entityId = transactionId;
        this.scheduleInitialPoll();
    }

    @Nullable
    @Override
    JSONObject makeApiCall() {
        try {
            JSONObject apiResponse = getApiClient().getTransaction( this.entityId );
            return getResult(apiResponse);
        } catch (Throwable th) {
            //Ignore
        }
        return null;
    }

    @Override
    boolean isOperationSuccessful(@Nullable JSONObject entity) {
        if ( !this.hasCalledMinedCallback && null != entity && TransactionStatus.MINED == this.getTransactionStatus( entity )) {
            triggerMinedCallback( entity );
        }
        return TransactionStatus.SUCCESS == this.getTransactionStatus( entity );
    }

    @Override
    boolean hasOperationFailed(@Nullable JSONObject entity) {
        return TransactionStatus.FAILED == this.getTransactionStatus( entity );
    }

    @Nullable
    @Override
    OstBaseEntity getBaseEntity(@Nullable JSONObject entity) {
        try {
            return new OstTransaction( entity );
        } catch (JSONException e) {
            return null;
        }
    }

    private void triggerMinedCallback(@NonNull JSONObject entityJson) {
        if ( this.hasCalledMinedCallback ) {
            return;
        }
        try {
            OstTransaction tx = new OstTransaction(entityJson);
            OstTransactionPollingCallback cb = (OstTransactionPollingCallback) getCallback();
            cb.onTransactionMined( tx );
            this.hasCalledMinedCallback = true;
        } catch (JSONException e) {
            // Ignore.
        }
    }

    private TransactionStatus getTransactionStatus(@Nullable JSONObject entityJson) {
        if ( null == entityJson ) {
            return TransactionStatus.UNKNOWN;
        }
        try {
            OstTransaction tx = new OstTransaction(entityJson);
            return tx.getTransactionStatus();
        } catch (JSONException e) {
            return TransactionStatus.UNKNOWN;
        }
    }
}
