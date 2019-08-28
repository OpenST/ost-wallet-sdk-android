/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.sdkInteract;

import android.util.Log;

import com.ost.walletsdk.ui.OstPassphraseAcceptor;
import com.ost.walletsdk.ui.OstUIWorkflowContext;
import com.ost.walletsdk.ui.OstUserPassphraseCallback;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.UUID;


public class WorkFlowListener implements OstWorkFlowCallback {

    private static final String LOG_TAG = "OstWorkFlowListener";
    private final SdkInteract mSdkInteract;
    private WeakReference<SdkInteract.WorkFlowCallbacks> mWorkflowCallbacks;

    private OstUserPassphraseCallback mUserPassphraseCallback;

    public String getId() {
        return mId;
    }

    private final String mId;

    WorkFlowListener() {
        this(null);
    }

    WorkFlowListener(SdkInteract.WorkFlowCallbacks workFlowCallbacks) {
        this.mWorkflowCallbacks = new WeakReference<>(workFlowCallbacks);
        this.mSdkInteract = SdkInteract.getInstance();
        this.mId = UUID.randomUUID().toString();
    }

    @Override
    public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {
        Log.i(LOG_TAG, String.format("Device Object %s ", apiParams.toString()));
    }

    @Override
    public void getPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        OstUIWorkflowContext uiContext = getContext(ostWorkflowContext);
        Log.d(LOG_TAG, String.format("Get Pin: WorkFlow Id: %s of Workflow %s", getId(), uiContext.getWorkflowType().toString()));

        if (null != mWorkflowCallbacks.get()) mWorkflowCallbacks.get().getPin(getId(), uiContext, userId, ostPinAcceptInterface);
    }

    @Override
    public void invalidPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        OstUIWorkflowContext uiContext = getContext(ostWorkflowContext);
        Log.d(LOG_TAG, String.format("Invalid Pin: WorkFlow Id: %s of Workflow %s", getId(), uiContext.getWorkflowType().toString()));
        if (null != mWorkflowCallbacks.get()) mWorkflowCallbacks.get().invalidPin(getId(), getContext(uiContext), userId, ostPinAcceptInterface);
    }

    @Override
    public void pinValidated(OstWorkflowContext ostWorkflowContext, String userId) {
        OstUIWorkflowContext uiContext = getContext(ostWorkflowContext);
        Log.d(LOG_TAG, String.format("Pin Validated: WorkFlow Id: %s of Workflow %s", getId(), uiContext.getWorkflowType().toString()));
        if (null != mWorkflowCallbacks.get()) mWorkflowCallbacks.get().pinValidated(getId(), uiContext, userId);
    }

    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        OstUIWorkflowContext uiContext = getContext(ostWorkflowContext);
        Log.d(LOG_TAG, String.format("Flow Complete: WorkFlow Id: %s of Workflow %s", getId(), uiContext.getWorkflowType().toString()));

        if (null != mWorkflowCallbacks.get()) {
            boolean consumed = mWorkflowCallbacks.get().flowComplete(getId(), getContext(uiContext), ostContextEntity);
            if (consumed) return;
        }

        mSdkInteract.notifyEvent(getId(), SdkInteract.CALLBACK_TYPE.FLOW_COMPLETE, uiContext, ostContextEntity);
        mSdkInteract.unRegister(this);
    }

    @Override
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        OstUIWorkflowContext uiContext = getContext(ostWorkflowContext);
        Log.d(LOG_TAG, String.format("Flow Interrupted: WorkFlow Id: %s of Workflow %s", getId(), uiContext.getWorkflowType().toString()));

        if (null != mWorkflowCallbacks.get()) {
            boolean consumed = mWorkflowCallbacks.get().flowInterrupt(getId(), getContext(uiContext), ostError);
            if (consumed) return;
        }

        mSdkInteract.notifyEvent(getId(), SdkInteract.CALLBACK_TYPE.FLOW_INTERRUPT,uiContext, ostError);
        mSdkInteract.unRegister(this);
    }

    @Override
    public void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        OstUIWorkflowContext uiContext = getContext(ostWorkflowContext);
        Log.d(LOG_TAG, String.format("Request Acknowledged: WorkFlow Id: %s of Workflow %s", getId(), uiContext.getWorkflowType().toString()));

        if (null != mWorkflowCallbacks.get()) {
            boolean consumed = mWorkflowCallbacks.get().requestAcknowledged(getId(), getContext(uiContext), ostContextEntity);
            if (consumed) return;
        }

        mSdkInteract.notifyEvent(getId(), SdkInteract.CALLBACK_TYPE.REQUEST_ACK, uiContext, ostContextEntity);
    }

    @Override
    public void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {
        OstUIWorkflowContext uiContext = getContext(ostWorkflowContext);
        Log.d(LOG_TAG, String.format("Verify Data: WorkFlow Id: %s of Workflow %s", getId(), uiContext.getWorkflowType().toString()));
        if (null != mWorkflowCallbacks.get()) mWorkflowCallbacks.get().verifyData(getId(), uiContext, ostContextEntity, ostVerifyDataInterface);
    }

    public void setUserPassPhraseCallback(OstUserPassphraseCallback userPassphraseCallback) {
        this.mUserPassphraseCallback = userPassphraseCallback;
    }

    public void getPassphrase(String userId, OstWorkflowContext ostWorkflowContext, OstPassphraseAcceptor ostPassphraseAcceptor) {
        OstUIWorkflowContext uiContext = getContext(ostWorkflowContext);
        if (null == mUserPassphraseCallback) {
            ostPassphraseAcceptor.cancelFlow();
        } else {
            this.mUserPassphraseCallback.getPassphrase(userId, uiContext, ostPassphraseAcceptor);
        }
    }

    public void setWorkflowCallbacks(SdkInteract.WorkFlowCallbacks workflowCallbacks) {
        this.mWorkflowCallbacks = new WeakReference<>(workflowCallbacks);
    }

    private OstUIWorkflowContext getContext(OstWorkflowContext context) {
        if ( context instanceof OstUIWorkflowContext ) {
            return (OstUIWorkflowContext) context;
        }
        return new OstUIWorkflowContext(context, this.getId());
    }
}