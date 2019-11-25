package com.ost.walletsdk.network.polling;

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;

import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.network.polling.interfaces.OstPollingCallback;

import org.json.JSONObject;

public class OstUserPollingHelper extends OstBasePollingHelper {
    public OstUserPollingHelper(@NonNull String userId, @NonNull OstPollingCallback callback) {
        super(userId, callback);
        this.setInitialPollingInterval( 2 * getInitialPollingInterval() );
        this.scheduleInitialPoll();
    }

    @Nullable
    @Override
    JSONObject makeApiCall() {
        try {
            JSONObject apiResponse = this.getApiClient().getUser();
            return getResult(apiResponse);
        } catch (Throwable th) {
            //Ignore.
        }
        return null;
    }

    @Override
    boolean isOperationSuccessful(@Nullable JSONObject entity) {
        return OstUser.UserStatus.ACTIVATED == this.getUserStatus();
    }

    @Override
    boolean hasOperationFailed(@Nullable JSONObject entity) {
        return OstUser.UserStatus.CREATED == this.getUserStatus();
    }

    @Nullable
    @Override
    OstBaseEntity getBaseEntity(@Nullable JSONObject entity) {
        return OstUser.getById(this.getUserId());
    }

    private OstUser.UserStatus getUserStatus() {
        OstUser user = OstUser.getById(this.getUserId());
        if ( null == user ) {
            return OstUser.UserStatus.UNKNOWN;
        }
        return user.getUserStatus();
    }
}
