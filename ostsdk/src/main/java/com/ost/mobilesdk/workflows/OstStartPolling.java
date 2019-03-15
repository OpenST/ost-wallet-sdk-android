/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.workflows;

import android.os.Bundle;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstSession;
import com.ost.mobilesdk.models.entities.OstTransaction;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstDevicePollingService;
import com.ost.mobilesdk.workflows.services.OstPollingService;
import com.ost.mobilesdk.workflows.services.OstSessionPollingService;
import com.ost.mobilesdk.workflows.services.OstTransactionPollingService;
import com.ost.mobilesdk.workflows.services.OstUserPollingService;

public class OstStartPolling extends OstBaseWorkFlow {

    private static final String TAG = "OstStartPolling";
    private final String mEntityId;
    private final String mEntityType;
    private final String mFromStatus;
    private final String mToStatus;

    public OstStartPolling(String userId, String entityId, String entityType, String fromStatus,
                           String toStatus, OstWorkFlowCallback callback) {
        super(userId, callback);
        mEntityId = entityId;
        mEntityType = entityType;
        mFromStatus = fromStatus;
        mToStatus = toStatus;
    }

    @Override
    protected AsyncStatus process() {

        Bundle bundle;
        Log.d(TAG, String.format("Polling workflow for userId: %s started", mUserId));

        Log.i(TAG, "validate params");
        if (!hasValidParams()) {
            return postErrorInterrupt("wf_sp_pr_1", OstErrors.ErrorCode.INVALID_WORKFLOW_PARAMS);
        }
        switch (mEntityType) {
            case OstSdk.USER:
                bundle = OstUserPollingService.startPolling(mUserId, mEntityId, mFromStatus, mToStatus);
                AsyncStatus userStatus = waitForUpdate(bundle);
                if (!userStatus.isSuccess()) return userStatus;

                return postFlowComplete(new OstContextEntity(OstUser.getById(mEntityId), mEntityType));

            case OstSdk.DEVICE:
                bundle = OstDevicePollingService.startPolling(mUserId, mEntityId, mFromStatus, mToStatus);
                AsyncStatus deviceStatus = waitForUpdate(bundle);
                if (!deviceStatus.isSuccess()) return deviceStatus;

                return postFlowComplete(new OstContextEntity(OstDevice.getById(mEntityId), mEntityType));

            case OstSdk.SESSION:
                bundle = OstSessionPollingService.startPolling(mUserId, mEntityId, mFromStatus, mToStatus);
                AsyncStatus sessionStatus = waitForUpdate(bundle);
                if (!sessionStatus.isSuccess()) return sessionStatus;

                return postFlowComplete(new OstContextEntity(OstSession.getById(mEntityId), mEntityType));

            case OstSdk.TRANSACTION:
                bundle = OstTransactionPollingService.startPolling(mUserId, mEntityId, mFromStatus, mToStatus);
                AsyncStatus transactionStatus = waitForUpdate(bundle);
                if (!transactionStatus.isSuccess()) return transactionStatus;

                return postFlowComplete(new OstContextEntity(OstTransaction.getById(mEntityId), mEntityType));
        }
        return postErrorInterrupt("wf_sp_pr_4", OstErrors.ErrorCode.UNKNOWN_ENTITY_TYPE);
    }

    private AsyncStatus waitForUpdate(Bundle bundle) {
        Log.i(TAG, "Waiting for update");
        if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
            Log.d(TAG, String.format("Polling time out for %s Id: %s", mEntityType, mEntityId));
            return postErrorInterrupt("wf_sp_pr_2", OstErrors.ErrorCode.POLLING_TIMEOUT);
        }
        if (!bundle.getBoolean(OstPollingService.EXTRA_IS_VALID_RESPONSE, false)) {
            Log.d(TAG, String.format("Polling time out for %s Id: %s", mEntityType, mEntityId));
            return postErrorInterrupt("wf_sp_pr_3", OstErrors.ErrorCode.POLLING_API_FAILED);
        }
        return new AsyncStatus(true);
    }
}