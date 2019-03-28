/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.network;

import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Sub class of {@link OstError}
 * This class object contains Errors thrown from OST Platform.
 */
public class OstApiError extends OstError {


    public String getErrCode() {
        return jsonApiError.optString("code");
    }

    public String getErrMsg() {
        return jsonApiError.optString("msg");
    }

    public JSONObject getJsonApiError() {
        return jsonApiError;
    }

    private JSONObject jsonApiError = null;
    public OstApiError(String internalErrorCode, OstErrors.ErrorCode errorCode, JSONObject apiResponse) {
        super(internalErrorCode, errorCode);
        setApiError( true );
        if ( apiResponse != null ) {
            jsonApiError = apiResponse.optJSONObject("err");
        }

        if ( null == jsonApiError ) {
            jsonApiError = new JSONObject();
        }

        parseErrorData();
    }

    public String getApiInternalId() {
        return jsonApiError.optString("internal_id");
    }

    public boolean isBadRequest() {
        return ApiErrorCodes.BAD_REQUEST.equalsIgnoreCase( getErrCode() );
    }

    public boolean isNotFound() {
        return ApiErrorCodes.NOT_FOUND.equalsIgnoreCase( getErrCode() );
    }

    public boolean isDeviceTimeOutOfSync() {
        ApiErrorData apiRequestTimestamp = getApiErrorData("api_request_timestamp");
        if ( null == apiRequestTimestamp ) {
            return false;
        }
        return true;
    }

    public boolean isApiSignerUnauthorized() {
        ApiErrorData apiKey = getApiErrorData("api_key");
        if ( null == apiKey ) {
            return false;
        }
        return true;
    }

    public ApiErrorData getApiErrorData(String parameterName) {
        int errorDataLength = errorDataList.size();
        int cnt = 0;
        while ( cnt < errorDataLength) {
            ApiErrorData errorData = errorDataList.get(cnt);
            if ( parameterName.equalsIgnoreCase(errorData.parameter) ) {
                return errorData;
            }
            cnt += 1;
        }
        return null;
    }

    public ArrayList<ApiErrorData> getErrorData() {
        return errorDataList;
    }

    private final ArrayList<ApiErrorData> errorDataList = new ArrayList<>();
    private void parseErrorData() {
        if ( null == jsonApiError ) {
            return;
        }

        JSONArray jsonErrorData = jsonApiError.optJSONArray("error_data");
        if ( null == jsonErrorData){
            return;
        }

        int errorDataLength = jsonErrorData.length();
        int cnt = 0;
        while ( cnt < errorDataLength) {
            JSONObject error = jsonErrorData.optJSONObject(cnt);
            if ( null == error ) {
                continue;
            }
            String eParameter = error.optString("parameter");
            String eMsg = error.optString("msg");
            if ( eParameter.length() < 1 && eMsg.length() < 1 ) {
                continue;
            }
            errorDataList.add( new ApiErrorData(eParameter, eMsg) );
            cnt += 1;
        }
    }

    public static class ApiErrorData {
        public String getParameter() {
            return parameter;
        }

        public String getMsg() {
            return msg;
        }

        private final String parameter;
        private final String msg;

        public ApiErrorData(String parameter, String msg) {
            this.parameter = parameter;
            this.msg = msg;
        }
    }

    public static class ApiErrorCodes {
        public static String BAD_REQUEST = "BAD_REQUEST";
        public static String AUTHENTICATION_ERROR = "AUTHENTICATION_ERROR";
        public static String NOT_FOUND = "NOT_FOUND";
        public static String INSUFFICIENT_FUNDS = "INSUFFICIENT_FUNDS";
        public static String UNPROCESSABLE_ENTITY = "UNPROCESSABLE_ENTITY";
        public static String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
        public static String AUTHORIZATION_ERROR = "AUTHORIZATION_ERROR";
        public static String REQUEST_TIMEOUT = "REQUEST_TIMEOUT";
        public static String UNSUPPORTED_VERSION = "UNSUPPORTED_VERSION";
        public static String TOO_MANY_REQUESTS = "TOO_MANY_REQUESTS";
        public static String ALREADY_EXISTS = "ALREADY_EXISTS";
    }
}
