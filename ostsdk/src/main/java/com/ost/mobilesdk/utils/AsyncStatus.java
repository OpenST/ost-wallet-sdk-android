package com.ost.mobilesdk.utils;

public class AsyncStatus {

    private final String mMessage;
    private final boolean mSuccess;

    public AsyncStatus(boolean success, String message){
        mSuccess = success;
        mMessage = message;
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