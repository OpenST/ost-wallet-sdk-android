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

/**
 * This class defines object of Ost Errors which are thrown on unexpected behaviour of wallet SDK.
 * This class object will provide Error code and  Internal error code
 */
public class OstError extends Error {

    private static String Tag = "OstError";

    private final OstErrors.OstErrorCode mErrorCode;
    private final String mInternalErrorCode;
    private boolean isApiError = false;

    public OstError(String internalErrorCode, OstErrors.OstErrorCode errorCode) {
        super(OstErrors.getMessage(errorCode));
        mErrorCode = errorCode;
        mInternalErrorCode = internalErrorCode;
        Log.d(Tag, "Error Code: '" + internalErrorCode + "'. Error Message:" + OstErrors.getMessage(errorCode));
    }

    public OstErrors.OstErrorCode getErrorCode() {
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
}