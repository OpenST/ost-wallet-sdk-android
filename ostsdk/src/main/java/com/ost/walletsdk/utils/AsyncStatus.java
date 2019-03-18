/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.utils;

import com.ost.walletsdk.workflows.errors.OstError;

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