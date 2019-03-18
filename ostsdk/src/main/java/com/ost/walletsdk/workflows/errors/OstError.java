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

public class OstError extends Error {

    private static String Tag = "OstError";

    private final OstErrors.ErrorCode mErrorCode;
    private final String mInternalErrorCode;
    private boolean isApiError = false;

    public OstError(String internalErrorCode, OstErrors.ErrorCode errorCode) {
        super(OstErrors.getMessage(errorCode));
        mErrorCode = errorCode;
        mInternalErrorCode = internalErrorCode;
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
}