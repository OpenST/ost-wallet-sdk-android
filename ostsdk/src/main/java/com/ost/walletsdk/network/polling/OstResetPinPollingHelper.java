package com.ost.walletsdk.network.polling;

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;

import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.models.entities.OstRecoveryOwner;
import com.ost.walletsdk.models.entities.OstRecoveryOwner.CONST_STATUS;
import com.ost.walletsdk.network.polling.interfaces.OstPollingCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class OstResetPinPollingHelper extends OstBasePollingHelper {
    private final String recoveryOwnerAddress;
    public OstResetPinPollingHelper(@NonNull String recoveryOwnerAddress, @NonNull String userId, @NonNull OstPollingCallback callback) {
        super(userId, callback);
        this.recoveryOwnerAddress = recoveryOwnerAddress;
        this.scheduleInitialPoll();
    }

    @Nullable
    @Override
    JSONObject makeApiCall() {
        JSONObject apiResponse = this.getApiClient().getRecoveryOwnerAddress( this.recoveryOwnerAddress );
        return getResult(apiResponse);
    }

    @Override
    boolean isOperationSuccessful(@Nullable JSONObject entity) {
        return CONST_STATUS.AUTHORIZED.equalsIgnoreCase( getRecoverOwnerStatus( entity ) );
    }

    @Override
    boolean hasOperationFailed(@Nullable JSONObject entity) {
        String entityStatus = getRecoverOwnerStatus( entity );
        return CONST_STATUS.AUTHORIZATION_FAILED.equalsIgnoreCase( entityStatus ) || CONST_STATUS.REVOKING.equalsIgnoreCase( entityStatus ) || CONST_STATUS.REVOKED.equalsIgnoreCase( entityStatus );
    }

    @Nullable
    @Override
    OstRecoveryOwner getBaseEntity(@Nullable JSONObject entity) {
        try {
            return new OstRecoveryOwner( entity );
        } catch (JSONException e) {
            return null;
        }
    }

    private String getRecoverOwnerStatus(@Nullable JSONObject entityJson) {
        if ( null == entityJson) {
            return CONST_STATUS.UNKNOWN;
        }
        OstRecoveryOwner entity = getBaseEntity( entityJson );
        if ( null == entity) {
            return CONST_STATUS.UNKNOWN;
        }

        return entity.getStatus();
    }
}
