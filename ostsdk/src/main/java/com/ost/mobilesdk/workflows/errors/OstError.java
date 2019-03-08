package com.ost.mobilesdk.workflows.errors;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;

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