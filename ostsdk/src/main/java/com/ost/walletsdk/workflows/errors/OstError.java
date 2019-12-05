/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.workflows.errors;

import android.util.Log;

import com.ost.walletsdk.annotations.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * This class defines object of Ost Errors which are thrown on unexpected behaviour of wallet SDK.
 * This class object will provide Error code and  Internal error code
 */
public class OstError extends Error {
    public static class OstJSONErrorKeys {
        public static String ERROR_CODE = "error_code";
        public static String INTERNAL_ERROR_CODE = "internal_error_code";
        public static String ERROR_MESSAGE = "error_message";
        public static String ERROR_INFO = "error_info";
        public static String API_ERROR = "api_error";
        public static String IS_API_ERROR = "is_api_error";
    }


    private static String Tag = "OstError";

    private final OstErrors.ErrorCode mErrorCode;
    private final String mInternalErrorCode;
    private final JSONObject mErrorInfo;
    private boolean isApiError = false;


    public OstError(String internalErrorCode, OstErrors.ErrorCode errorCode) {
        this(internalErrorCode,errorCode,null);
    }

    public OstError(String internalErrorCode, OstErrors.ErrorCode errorCode, JSONObject errorInfo ) {
        super(OstErrors.getMessage(errorCode));
        mErrorCode = errorCode;
        mInternalErrorCode = internalErrorCode;
        if ( null == errorInfo ) {
            mErrorInfo = new JSONObject();
        } else {
            mErrorInfo = errorInfo;
        }

        Log.d(Tag, "Error Code: '" + internalErrorCode + "'. Error Message:" + OstErrors.getMessage(errorCode));
    }

    public OstErrors.ErrorCode getErrorCode() {
        return mErrorCode;
    }

    public String getInternalErrorCode() {
        return mInternalErrorCode;
    }

    public boolean isApiError() {
        return isApiError;
    }

    protected void setApiError(boolean apiError) {
        isApiError = apiError;
    }


    public JSONObject toJSONObject() {
        JSONObject err = new JSONObject();
        try {
            err.putOpt(OstJSONErrorKeys.ERROR_CODE,  OstErrors.errorCodeToString(mErrorCode));
        } catch (JSONException e) {
            //Ignore.
        }

        try {
            err.putOpt(OstJSONErrorKeys.INTERNAL_ERROR_CODE, mInternalErrorCode);
        } catch (JSONException e) {
            //Ignore.
        }

        try {
            err.putOpt(OstJSONErrorKeys.ERROR_MESSAGE, OstErrors.getMessage(mErrorCode) );
        } catch (JSONException e) {
            //Ignore.
        }

        if ( null != mErrorInfo ) {
            try {
                err.putOpt(OstJSONErrorKeys.ERROR_INFO, mErrorInfo);
            } catch (JSONException e) {
                //Ignore.
            }
        }

        try {
            err.putOpt(OstJSONErrorKeys.IS_API_ERROR, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return err;
    }

    public void addErrorInfo(@NonNull String infoKey, @NonNull String data ) {
        if ( null == data ) {
            data = "NULL";
        }

        try {
            mErrorInfo.putOpt(infoKey, data);
        } catch (JSONException e) {
            //can't do anything. Ignore.
        }
    }

    public static OstError ApiResponseError(String internalErrorCode, String apiMethodName, JSONObject apiResponse) {
        JSONObject errorInfo = new JSONObject();

        try {
            errorInfo.putOpt("apiMethodName", apiMethodName);
        } catch (JSONException e) {
            //Ignore.
        }

        try {
            errorInfo.putOpt("apiResponse", apiResponse);
        } catch (JSONException e) {
            //Ignore.
        }

        return new OstError(internalErrorCode, OstErrors.ErrorCode.INVALID_API_RESPONSE, errorInfo);
    }

    public static OstError SdkError(@NonNull String internalErrorCode, @NonNull Throwable th) {
        if ( th instanceof OstError ) {
            //The error is already an OST error.
            return (OstError) th;
        }

        JSONObject errorInfo = new JSONObject();
        try {
            StringWriter sw = new StringWriter();
            th.printStackTrace(new PrintWriter(sw));
            String stackTraceAsString = sw.toString();
            errorInfo.putOpt("stack_trace", stackTraceAsString);

        } catch (Throwable error) {
            error.printStackTrace();
            //can't do anything. Ignore.
        }

        try {
            errorInfo.putOpt("error_message", th.getMessage());
        } catch (Throwable error) {
            error.printStackTrace();
            //can't do anything. Ignore.
        }

        return new OstError(internalErrorCode, OstErrors.ErrorCode.SDK_ERROR, errorInfo);
    }
}