package com.ost.mobilesdk.network;

import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;

import org.json.JSONObject;

public class OstApiError extends OstError {
    public JSONObject getJsonApiError() {
        return jsonApiError;
    }

    private final JSONObject jsonApiError;
    public OstApiError(String internalErrorCode, OstErrors.ErrorCode errorCode, JSONObject apiError) {
        super(internalErrorCode, errorCode);
        this.jsonApiError = apiError;
        setApiError( true );
    }
}
