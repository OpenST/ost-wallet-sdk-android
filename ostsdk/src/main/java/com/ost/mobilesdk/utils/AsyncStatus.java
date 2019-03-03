package com.ost.mobilesdk.utils;

import com.ost.mobilesdk.workflows.errors.OstError;

public class AsyncStatus {

    private final String mMessage;
    private final boolean mSuccess;

    public AsyncStatus(boolean success, String message){
        mSuccess = success;
        mMessage = message;
    }

    public AsyncStatus(OstError error) {
        mSuccess = false;
        mMessage = error.getMessage();
    }

    public AsyncStatus(boolean success){
        this(success,"");
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getMessage() {
        return mMessage;
    }
}