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

        Log.d(TAG, String.format("Polling workflow for userId: %s started", mUserId));

        Log.i(TAG, "validate params");
        if (!hasValidParams()) {
            return postErrorInterrupt("wf_sp_pr_1", OstErrors.ErrorCode.INVALID_WORKFLOW_PARAMS);
        }
        switch (mEntityType) {
            case OstSdk.USER:
                OstUserPollingService.startPolling(mUserId, mEntityId, mFromStatus, mToStatus);
                AsyncStatus userStatus = waitForUpdate();
                if (!userStatus.isSuccess()) return userStatus;

                return postFlowComplete(new OstContextEntity(OstUser.getById(mEntityId), mEntityType));

            case OstSdk.DEVICE:
                OstDevicePollingService.startPolling(mUserId, mEntityId, mFromStatus, mToStatus);
                AsyncStatus deviceStatus = waitForUpdate();
                if (!deviceStatus.isSuccess()) return deviceStatus;

                return postFlowComplete(new OstContextEntity(OstDevice.getById(mEntityId), mEntityType));

            case OstSdk.SESSION:
                OstSessionPollingService.startPolling(mUserId, mEntityId, mFromStatus, mToStatus);
                AsyncStatus sessionStatus = waitForUpdate();
                if (!sessionStatus.isSuccess()) return sessionStatus;

                return postFlowComplete(new OstContextEntity(OstSession.getById(mEntityId), mEntityType));

            case OstSdk.TRANSACTION:
                OstTransactionPollingService.startPolling(mUserId, mEntityId, mFromStatus, mToStatus);
                AsyncStatus transactionStatus = waitForUpdate();
                if (!transactionStatus.isSuccess()) return transactionStatus;

                return postFlowComplete(new OstContextEntity(OstTransaction.getById(mEntityId), mEntityType));
        }
        return postErrorInterrupt("wf_sp_pr_4", OstErrors.ErrorCode.UNKNOWN_ENTITY_TYPE);
    }

    private AsyncStatus waitForUpdate() {
        Log.i(TAG, "Waiting for update");
        Bundle bundle = waitForUpdate(mEntityType, mEntityId);
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

    @Override
    boolean hasValidParams() {
        return super.hasValidParams();
    }
}