package com.ost.mobilesdk.workflows.errors;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;

public class OstError extends Error {

    private static String Tag = "OstError";
    private final OstConstants.WORKFLOW_TYPE mFlowType;
    private final OstErrors.ErrorCode mErrorCode;
    private final String mInternalErrorCode;


    public OstError(String errorMsg) {
        super(errorMsg);
        mFlowType = OstConstants.WORKFLOW_TYPE.UNKNOWN;
        mErrorCode = OstErrors.ErrorCode.UNKNOWN;
        mInternalErrorCode = "";
    }

    public OstError(String errorMsg, OstConstants.WORKFLOW_TYPE flowType) {
        super(errorMsg);
        mFlowType = flowType;
        mErrorCode = OstErrors.ErrorCode.UNKNOWN;
        mInternalErrorCode = "";
    }

    public OstError(String internalErrorCode, OstErrors.ErrorCode errorCode, OstConstants.WORKFLOW_TYPE flowType) {
        super(OstErrors.getMessage(errorCode));
        mFlowType = flowType;
        mErrorCode = errorCode;
        mInternalErrorCode = internalErrorCode;
        Log.e(Tag, "\n\n\nError Code" + internalErrorCode + " \n\n\n");
    }

    public OstConstants.WORKFLOW_TYPE getFlowType() {
        return mFlowType;
    }


    public OstErrors.ErrorCode getErrorCode() {
        return mErrorCode;
    }
}