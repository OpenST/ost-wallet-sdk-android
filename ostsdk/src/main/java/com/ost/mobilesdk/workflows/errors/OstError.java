package com.ost.mobilesdk.workflows.errors;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;

import java.util.HashMap;
import java.util.Map;

public class OstError extends Error {

    private static String Tag = "OstError";
    private final OstConstants.WORKFLOW_TYPE mFlowType;
    private final String mErrorCode;


    public OstError(String errorMsg) {
        super(errorMsg);
        mFlowType = OstConstants.WORKFLOW_TYPE.UNKNOWN;
        mErrorCode = "";
    }

    public OstError(String errorMsg, OstConstants.WORKFLOW_TYPE flowType) {
        super(errorMsg);
        mFlowType = flowType;
        mErrorCode = "";
    }

    public OstError(String errorCode, String errorMsg, OstConstants.WORKFLOW_TYPE flowType) {
        super(errorMsg);
        mFlowType = flowType;
        mErrorCode = errorCode;
        Log.e(Tag, "\n\n\nError Code" + errorCode + " \n\n\n");
    }

    public OstConstants.WORKFLOW_TYPE getFlowType() {
        return mFlowType;
    }


    public String getErrorCode() {
        return mErrorCode;
    }
}