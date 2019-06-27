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

import android.os.Bundle;
import android.system.Os;
import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.OstMultiSigSigner;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedLogoutSessionsStruct;
import com.ost.walletsdk.models.Impls.OstModelFactory;
import com.ost.walletsdk.models.Impls.OstSessionKeyModelRepository;
import com.ost.walletsdk.models.entities.OstDeviceManager;
import com.ost.walletsdk.models.entities.OstTokenHolder;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.utils.OstPayloadBuilder;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.walletsdk.workflows.services.OstPollingService;
import com.ost.walletsdk.workflows.services.OstTokenHolderPollingService;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * It will revoke all the sessions associated with provided userId
 */
public class OstLogoutAllSessions extends OstBaseWorkFlow {

    private static final String TAG = "OstLogoutAllSessions";

    public OstLogoutAllSessions(String userId, OstWorkFlowCallback callback) {
        super(userId, callback);
    }

    @Override
    protected boolean shouldAskForAuthentication() {
        return false;
    }

    @Override
    AsyncStatus performOnAuthenticated() {

        //Sync device Manager to get update nonce for multi-sig operation
        syncDeviceManager();

        Log.i(TAG, "Getting Signed logoutAllSession struct");
        OstMultiSigSigner sigSigner = new OstMultiSigSigner(mUserId);
        SignedLogoutSessionsStruct logoutSessionsStruct = sigSigner.logoutAllSessions();

        Log.i(TAG, "Building request payload for post request");
        Map<String, Object> requestMap = buildPostLogoutPayload(logoutSessionsStruct);

        Log.i(TAG, "Posting logout all sessions request");
        postLogoutRequest(requestMap);

        Log.i(TAG, "Request Acknowledged");
        postRequestAcknowledge(
                new OstWorkflowContext(getWorkflowType()),
                new OstContextEntity(mOstUser.getTokenHolder(), OstSdk.TOKEN_HOLDER)
        );

        Log.i(TAG, "wiping all local sessions");
        wipeAllLocalSessions();

        Log.i(TAG, "Increment Nonce");
        OstDeviceManager manager = OstDeviceManager.getById(mOstUser.getDeviceManagerAddress());
        if (null != manager) manager.incrementNonce();

        Log.i(TAG, "wait for status update for Logged out");
        waitForStatusUpdate();

        postFlowComplete(
                new OstContextEntity(mOstUser.getTokenHolder(), OstSdk.TOKEN_HOLDER)
        );

        return super.performOnAuthenticated();
    }

    private void wipeAllLocalSessions() {
        OstModelFactory.getSessionModel().deleteAllEntities();
        new OstSessionKeyModelRepository().deleteAllSessionKeys();
    }

    private void waitForStatusUpdate() {
        Bundle bundle = OstTokenHolderPollingService.startPolling(
                mUserId,
                mOstUser.getTokenHolderAddress(),
                OstTokenHolder.CONST_STATUS.LOGGED_OUT,
                OstTokenHolder.CONST_STATUS.ACTIVE
        );

        if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
            throw new OstError("wf_loas_pr_3", OstErrors.ErrorCode.POLLING_TIMEOUT);
        }
        if (!bundle.getBoolean(OstPollingService.EXTRA_IS_VALID_RESPONSE, false)) {
            Log.i(TAG, "Not a valid response");
            throw OstError.ApiResponseError("wf_loas_pr_4", "OstTokenHolderPollingService",null );
        }
    }

    private void postLogoutRequest(Map<String, Object> requestMap) {

        JSONObject responseObject = mOstApiClient.postLogoutAllSessions(requestMap);
        Log.i(TAG, String.format("Response %s", responseObject.toString()));

        if (!isValidResponse(responseObject)) {
            //postLogoutAllSessions will throw OstApiError
            throw new OstError("wf_loas_pr_2", OstErrors.ErrorCode.WORKFLOW_FAILED);
        }
    }

    private Map<String, Object> buildPostLogoutPayload(SignedLogoutSessionsStruct logoutSessionsStruct) {
        return new OstPayloadBuilder()
                .setRawCalldata(logoutSessionsStruct.getRawCallData())
                .setCallData(logoutSessionsStruct.getCallData())
                .setTo(logoutSessionsStruct.getTokenHolderAddress())
                .setSignatures(logoutSessionsStruct.getSignature())
                .setSigners(Collections.singletonList(logoutSessionsStruct.getSignerAddress()))
                .setNonce(logoutSessionsStruct.getNonce())
                .build();
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.LOGOUT_ALL_SESSIONS;
    }
}