package com.ost.walletsdk.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors.ErrorCode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class OstJsonApi {
    private final static ThreadPoolExecutor REQUEST_API_THREAD_POOL_EXECUTOR = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(1);

    private static ThreadPoolExecutor getAsyncQueue() {
        return REQUEST_API_THREAD_POOL_EXECUTOR;
    }

    // region - getBalance

    /**
     * Api to get user balance. Balance of only current logged-in user can be fetched.
     *
     * @param userId User Id of the current logged-in user.
     * @param callback callback where to receive data/error.
     */
    public static void getBalance(@NonNull String userId, @NonNull OstJsonApiCallback callback) {
        getAsyncQueue().submit(new Runnable() {
            @Override
            public void run() {
                execGetBalance(userId, callback);
            }
        });
    }

    private static void execGetBalance(@NonNull String userId, @NonNull OstJsonApiCallback callback) {
        JSONObject data = null;
//        if ( null == userId ) {
//            callback.onOstJsonApiError(new OstError("ojsonapi_egb_1", ErrorCode.INVALID_USER_ID ), data);
//        }

        try {
            //TODO: Remove this try catch once react changes come here.
            OstApiClient apiClient = new OstApiClient(userId);
            JSONObject response = apiClient.getBalance();
            data = getDataFromApiResponse( response );
            callback.onOstJsonApiSuccess( data );
        } catch (Throwable err) {
            OstError error = null;
            if ( err instanceof OstError ) {
                error = (OstError) err;
            } else {
                //TODO: Throw invalid api response error.
                error = new OstError("ojsonapi_egb_2", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            }
            callback.onOstJsonApiError(error, data);
        }
    }
    // endregion

    // region - getBalanceWithPricePoints

    /**
     * Api to get user balance and Price Points. Balance of only current logged-in user can be fetched.
     *
     * @param userId User Id of the current logged-in user.
     * @param callback callback where to receive data/error.
     */
    public static void getBalanceWithPricePoints(@NonNull String userId, @NonNull OstJsonApiCallback callback) {
        getAsyncQueue().submit(new Runnable() {
            @Override
            public void run() {
                execGetBalanceWithPricePoints(userId, callback);
            }
        });
    }

    private static void execGetBalanceWithPricePoints(@NonNull String userId, @NonNull OstJsonApiCallback callback) {
        JSONObject data = new JSONObject();
//        if ( null == userId ) {
//            callback.onOstJsonApiError(new OstError("ojsonapi_egbwpp_1", ErrorCode.INVALID_USER_ID ), data);
//        }

        try {
            //TODO: Remove this try catch once react changes come here.
            OstApiClient apiClient = new OstApiClient(userId);

            //Get Balance.
            JSONObject balanceResponse = apiClient.getBalance();
            JSONObject balanceData = getDataFromApiResponse( balanceResponse );
            if ( null == balanceData ) {
                //TODO: Throw invalid api response error.
                throw new OstError("ojsonapi_egbwpp_2", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            }
            String balanceResultType = getResultType( balanceData );
            //Populate data.
            data.putOpt( balanceResultType, balanceData );
            data.putOpt("result_type", balanceResultType);

            //Get Price Point
            JSONObject pricePointResponse = apiClient.getPricePoints();
            JSONObject pricePointData = getDataFromApiResponse( pricePointResponse );
            if ( null == pricePointData ) {
                //TODO: Throw invalid api response error.
                throw new OstError("ojsonapi_egbwpp_3", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            }
            String pricePointResultType = getResultType( pricePointData );

            //Populate data.
            data.putOpt( pricePointResultType, pricePointData);

            callback.onOstJsonApiSuccess( data );
        } catch (Throwable err) {
            OstError error = null;
            if ( err instanceof OstError ) {
                error = (OstError) err;
            } else {

                error = new OstError("ojsonapi_egbwpp_4", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            }
            callback.onOstJsonApiError(error, data);
        }
    }
    // endregion

    // region - getTransactions

    /**
     * Api to get user transactions. Transactions of only current logged-in user can be fetched.
     *
     * @param userId User Id of the current logged-in user.
     * @param requestPayload request payload. Such as next-page payload, filters etc.
     * @param callback callback where to receive data/error.
     */
    public static void getTransactions(@NonNull String userId, @Nullable Map<String, Object> requestPayload, @NonNull OstJsonApiCallback callback) {
        getAsyncQueue().submit(new Runnable() {
            @Override
            public void run() {
                execGetTransactions(userId, requestPayload, callback);
            }
        });
    }

    private static void execGetTransactions(@NonNull String userId, Map<String, Object> requestPayload,  @NonNull OstJsonApiCallback callback) {
        JSONObject data = null;
//        if ( null == userId ) {
//            callback.onOstJsonApiError(new OstError("ojsonapi_egt_1", ErrorCode.INVALID_USER_ID ), data);
//        }

        try {
            //TODO: Remove this try catch once react changes come here.
            OstApiClient apiClient = new OstApiClient(userId);
            JSONObject response = apiClient.getTransactions(requestPayload);
            data = getDataFromApiResponse( response );
            callback.onOstJsonApiSuccess( data );
        } catch (Throwable err) {
            OstError error = null;
            if ( err instanceof OstError ) {
                error = (OstError) err;
            } else {
                //TODO: Throw invalid api response error.
                error = new OstError("ojsonapi_egt_2", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            }
            callback.onOstJsonApiError(error, data);
        }
    }
    // endregion

    // region - getPendingRecovery
    /**
     * Api to get user balance. Balance of only current logged-in user can be fetched.
     *
     * @param userId User Id of the current logged-in user.
     * @param callback callback where to receive data/error.
     */
    public static void getPendingRecovery(@NonNull String userId, @NonNull OstJsonApiCallback callback) {
        getAsyncQueue().submit(new Runnable() {
            @Override
            public void run() {
                execGetBalance(userId, callback);
            }
        });
    }

    private static void execGetPendingRecovery(@NonNull String userId, @NonNull OstJsonApiCallback callback) {
        JSONObject data = null;
//        if ( null == userId ) {
//            callback.onOstJsonApiError(new OstError("ojsonapi_egpr_1", ErrorCode.INVALID_USER_ID ), data);
//        }

        try {
            //TODO: Remove this try catch once react changes come here.
            OstApiClient apiClient = new OstApiClient(userId);
            JSONObject response = apiClient.getPendingRecovery();
            data = getDataFromApiResponse( response );
            callback.onOstJsonApiSuccess( data );
        } catch (Throwable err) {
            OstError error = null;
            if ( err instanceof OstError ) {
                error = (OstError) err;
            } else {
                //TODO: Throw invalid api response error.
                error = new OstError("ojsonapi_egpr_2", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            }
            callback.onOstJsonApiError(error, data);
        }
    }
    // endregion


    // region - Helper Methods
    public static @Nullable String getResultType(@NonNull JSONObject data) {
//        if ( null == data ) { return  null;}
        return data.optString("result_type");
    }

    public static @Nullable JSONObject getResultJsonObject(@NonNull JSONObject data) {
//        if ( null == data ) { return  null;}
        String resultType = getResultType(data);
        if ( null == resultType ) { return null; }
        return data.optJSONObject(resultType);
    }

    public static @Nullable JSONArray getResultJsonArray(@NonNull JSONObject data) {
//        if ( null == data ) { return  null;}

        String resultType = getResultType(data);
        if ( null == resultType ) { return null; }

        return data.optJSONArray( resultType );
    }

    public static @Nullable JSONObject getDataFromApiResponse(@NonNull JSONObject apiResponse) {
        return apiResponse.optJSONObject("data");
    }
    // endregion
}