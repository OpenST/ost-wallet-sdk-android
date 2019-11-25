package com.ost.walletsdk.network.polling;

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;

import com.ost.walletsdk.OstConfigs;
import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.network.OstApiClient;
import com.ost.walletsdk.network.polling.interfaces.OstPollingCallback;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors.ErrorCode;

import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class OstBasePollingHelper {
    private final static ScheduledExecutorService POLLING_REQUEST_API_THREAD_POOL_EXECUTOR = Executors
            .newScheduledThreadPool(1);
    private static final String TAG = "OstBasePollingHelper";
    private long pollingInterval = OstConfigs.getInstance().getBLOCK_GENERATION_TIME() * 1000;;
    private long initialPollingInterval = pollingInterval * 6;
    private int maxPollingCount = 20;
    private int currentPollingIndex = 0;
    private OstPollingCallback callback;
    private final String userId;

    private OstApiClient apiClient;


    OstBasePollingHelper(@NonNull String userId, @NonNull OstPollingCallback callback) {
        this.callback = callback;
        this.userId = userId;
        this.apiClient = new OstApiClient(userId);
    }

    abstract @Nullable JSONObject makeApiCall();
    abstract boolean isOperationSuccessful(@Nullable JSONObject entity);
    abstract boolean hasOperationFailed(@Nullable JSONObject entity);
    abstract @Nullable OstBaseEntity getBaseEntity(@Nullable JSONObject entity);

    public void scheduleInitialPoll() {
        schedule(getInitialPollingInterval());
    }

    void scheduleNextPoll() {
        schedule(getPollingInterval());
    }

    void schedule(long delay) {
        OstBasePollingHelper oThis = this;
        getAsyncQueue().schedule(new Runnable() {
            @Override
            public void run() {
                oThis.executePoll();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    void executePoll() {
        try {
            JSONObject entity = this.makeApiCall();
            if ( isOperationSuccessful(entity) ) {
                onOperationSuccessful(entity);
                return;
            }
            if ( hasOperationFailed(entity) ) {
                onOperationFailed(entity);
                return;
            }

        } catch (Throwable t) {
            //Ignore errors.
        }

        //Increment the polling index.
        currentPollingIndex = currentPollingIndex + 1;
        if ( currentPollingIndex >= getMaxPollingCount() ) {
            onPollingTimedOut();
            return;
        }

        //Schedule the poll.
        this.scheduleNextPoll();
    }

    void onOperationSuccessful(JSONObject entityJson) {
        //Get callback
        OstPollingCallback callback = getCallback();

        //Clean-Up
        cleanUp();

        //Finally. trigger callback.
        OstBaseEntity entity = getBaseEntity(entityJson);
        callback.onOstPollingSuccess(entity, entityJson);
    }

    void onPollingTimedOut() {
        //Get callback
        OstPollingCallback callback = getCallback();

        //Clean-Up
        cleanUp();

        //Finally. trigger callback.
        callback.onOstPollingFailed(new OstError("obph_oof_1", ErrorCode.POLLING_TIMEOUT));
    }

    void onOperationFailed(JSONObject entity) {
        //Get callback
        OstPollingCallback callback = getCallback();

        //Clean-Up
        cleanUp();

        //Finally. trigger callback.
        callback.onOstPollingFailed(new OstError("obph_oof_1", ErrorCode.WORKFLOW_FAILED));
    }

    void cleanUp() {
        this.callback = null;
        this.apiClient = null;
    }

    // region - Getter & Setters
    public long getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public long getInitialPollingInterval() {
        return initialPollingInterval;
    }

    public void setInitialPollingInterval(long initialPollingInterval) {
        this.initialPollingInterval = initialPollingInterval;
    }

    public int getMaxPollingCount() {
        return maxPollingCount;
    }

    public void setMaxPollingCount(int maxPollingCount) {
        this.maxPollingCount = maxPollingCount;
    }

    public OstPollingCallback getCallback() {
        return callback;
    }

    protected ScheduledExecutorService getAsyncQueue() {
        return POLLING_REQUEST_API_THREAD_POOL_EXECUTOR;
    }

    public int getCurrentPollingIndex() {
        return currentPollingIndex;
    }

    public String getUserId() {
        return userId;
    }

    public OstApiClient getApiClient() {
        return apiClient;
    }
    // endregion

    // region - helper methods
    public static JSONObject getResultTypeFromApiResponse(JSONObject apiResponse) {
        if ( null == apiResponse ) {
            return null;
        }

        JSONObject data = apiResponse.optJSONObject("data");
        if ( null == data ) {
            return null;
        }

        String resultType = data.optString("result_type");
        if ( null == resultType ) {
            return null;
        }

        return data.optJSONObject( resultType );
    }

    public JSONObject getResult(JSONObject apiResponse) {
        return OstBasePollingHelper.getResultTypeFromApiResponse( apiResponse );
    }
    // endregion

}
