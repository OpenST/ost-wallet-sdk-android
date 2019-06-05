/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.workflows;

import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import java.util.ArrayList;

class WorkflowStateManager {
    ArrayList<String> orderedStates = new ArrayList<>();
    public static final String INITIAL = "INITIAL";
    public static final String PARAMS_VALIDATED = "PARAMS_VALIDATED";
    public static final String INITIALIZED = "INITIALIZED";
    public static final String REGISTERED = "REGISTERED";
    public static final String DEVICE_VALIDATED = "DEVICE_VALIDATED";
    public static final String PIN_AUTHENTICATION_REQUIRED = "PIN_AUTHENTICATION_REQUIRED";
    public static final String PIN_INFO_RECEIVED = "PIN_INFO_RECEIVED";
    public static final String AUTHENTICATED = "AUTHENTICATED";
    public static final String CANCELLED = "CANCELLED";
    public static final String COMPLETED_WITH_ERROR = "COMPLETED_WITH_ERROR";
    public static final String COMPLETED = "COMPLETED";
    public static final String VERIFY_DATA = "VERIFY_DATA";
    public static final String DATA_VERIFIED = "DATA_VERIFIED";
    public static final String CALLBACK_LOST = "CALLBACK_LOST";


    private int mCurrentState = 0;


    private Object mStateObject = null;

    public WorkflowStateManager() {
        setSateOrder();
    }

    public void setSateOrder() {
        orderedStates.add(INITIAL);
        orderedStates.add(PARAMS_VALIDATED);
        orderedStates.add(DEVICE_VALIDATED);
        orderedStates.add(PIN_AUTHENTICATION_REQUIRED);
        orderedStates.add(PIN_INFO_RECEIVED);
        orderedStates.add(AUTHENTICATED);
        orderedStates.add(CANCELLED);
        orderedStates.add(COMPLETED);
        orderedStates.add(COMPLETED_WITH_ERROR);
        orderedStates.add(CALLBACK_LOST);
    }


    public String getCurrentState() {
        return orderedStates.get(mCurrentState);
    }

    public Object getStateObject() {
        return mStateObject;
    }

    public String getNextState() {
        return orderedStates.get(mCurrentState + 1);
    }

    public void setCurrentStateObject(Object stateObject) {
        this.mStateObject = stateObject;
    }

    public void setNextState(Object stateObject) {
        mCurrentState += 1;
        this.mStateObject = stateObject;
    }
    public void setNextState() {
        setNextState(null);
    }

    public void setState(String state) {
        setState(state, null);
    }
    public void setState(String state, Object stateObject) {
        int stateIndx = orderedStates.indexOf(state);
        if ( stateIndx < 0 ) {
            OstError ostError = new OstError("bua_wf_WFSM_jts_1", OstErrors.ErrorCode.SDK_ERROR);
            throw ostError;
        }
        mCurrentState = stateIndx;
        mStateObject = stateObject;
    }
}
