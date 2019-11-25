package com.ost.walletsdk.network.polling;

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;

import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.network.polling.interfaces.OstPollingCallback;

import org.json.JSONObject;

public class OstDevicePollingHelper extends OstBasePollingHelper {
    private final String mDeviceAddress;

    public OstDevicePollingHelper(@NonNull String userId, @NonNull String deviceAddress , @NonNull OstPollingCallback callback) {
        super(userId, callback);
        this.setInitialPollingInterval( getInitialPollingInterval() );
        this.scheduleInitialPoll();
        this.mDeviceAddress = deviceAddress;
    }

    @Override
    public long getInitialPollingInterval() {
        return 3;
    }

    @Nullable
    @Override
    JSONObject makeApiCall() {
        try {
            JSONObject apiResponse = this.getApiClient().getDevice(mDeviceAddress);
            return getResult(apiResponse);
        } catch (Throwable th) {
            //Ignore.
        }
        return null;
    }

    @Override
    boolean isOperationSuccessful(@Nullable JSONObject entity) {
        return OstDevice.CONST_STATUS.AUTHORIZED.equalsIgnoreCase(this.getDeviceStatus());
    }

    @Override
    boolean hasOperationFailed(@Nullable JSONObject entity) {
        return OstDevice.CONST_STATUS.REGISTERED.equalsIgnoreCase(this.getDeviceStatus());
    }

    @Nullable
    @Override
    OstBaseEntity getBaseEntity(@Nullable JSONObject entity) {
        return OstDevice.getById(mDeviceAddress);
    }

    private String getDeviceStatus() {
        OstDevice device = OstDevice.getById(mDeviceAddress);
        if (null == device) return "UNKNOWN";
        return device.getStatus();
    }
}