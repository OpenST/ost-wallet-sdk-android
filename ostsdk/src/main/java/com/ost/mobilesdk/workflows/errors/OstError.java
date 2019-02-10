package com.ost.mobilesdk.workflows.errors;

public class OstError extends Error {

    enum FLOW_TYPE {
        UNKNOWN,
        LOGIN,
        TRANSACTION,
        CREATE_SESSION,
        REGISTER_DEVICE,
        TOKEN_HOLDER_DEPLOYMENT,
    }

    enum SUB_TYPE {
        UNKNOWN,
        ID_ERROR
    }

    private FLOW_TYPE mFlowType;
    private SUB_TYPE mSubType;

    public OstError(String errorMsg) {
        super(errorMsg);
        mFlowType = FLOW_TYPE.UNKNOWN;
        mSubType = SUB_TYPE.UNKNOWN;
    }

    public OstError(String errorMsg, FLOW_TYPE flowType, SUB_TYPE subType) {
        super(errorMsg);
        mFlowType = flowType;
        mSubType = subType;
    }

    public FLOW_TYPE getFlowType() {
        return mFlowType;
    }

    public SUB_TYPE getSubType() {
        return mSubType;
    }
}