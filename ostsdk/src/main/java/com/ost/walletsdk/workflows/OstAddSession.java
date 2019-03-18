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
import android.util.Log;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.OstKeyManager;
import com.ost.walletsdk.ecKeyInteracts.OstMultiSigSigner;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedAddSessionStruct;
import com.ost.walletsdk.models.entities.OstDeviceManager;
import com.ost.walletsdk.models.entities.OstSession;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.network.OstApiClient;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.utils.GnosisSafe;
import com.ost.walletsdk.utils.OstPayloadBuilder;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.walletsdk.workflows.services.OstPollingService;
import com.ost.walletsdk.workflows.services.OstSessionPollingService;

import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;

/**
 * To Add Session
 * 1. param validation
 * 2. user activated and device authorized
 * 3. create session keys
 * 4. create payload
 * 5. api post call
 * 6. polling
 */
public class OstAddSession extends OstBaseUserAuthenticatorWorkflow implements OstPinAcceptInterface {

    private static final String TAG = "OstAddSession";
    private final String mSpendingLimit;
    private final long mExpiresAfterInSecs;

    public OstAddSession(String userId, String spendingLimit, long expiresAfterInSecs, OstWorkFlowCallback callback) {
        super(userId, callback);
        mSpendingLimit = spendingLimit;
        mExpiresAfterInSecs = expiresAfterInSecs;
    }



    @Override
    AsyncStatus performOnAuthenticated() {
        String sessionAddress = new OstKeyManager(mUserId).createSessionKey();

        OstApiClient ostApiClient = new OstApiClient(mUserId);

        Log.i(TAG, "Getting current block number");
        String blockNumber = getCurrentBlockNumber(ostApiClient);
        if (null == blockNumber) {
            Log.e(TAG, "BlockNumber is null");
            return postErrorInterrupt("wf_as_pr_1", OstErrors.ErrorCode.BLOCK_NUMBER_API_FAILED);
        }

        OstUser ostUser = OstUser.getById(mUserId);
        String tokenHolderAddress = ostUser.getTokenHolderAddress();
        String deviceManagerAddress = ostUser.getDeviceManagerAddress();


        //TODO: FIX-ME: Divide mExpiresAfterInSec by Block generation time
        String expiryHeight = new BigInteger(blockNumber).add(new BigInteger(String
                .valueOf(mExpiresAfterInSecs))).toString();

        try {
            ostApiClient.getDeviceManager();
        } catch (IOException e) {
            Log.e(TAG, "IO Exception ");
        }

        OstMultiSigSigner signer = new OstMultiSigSigner(mUserId);
        SignedAddSessionStruct struct;
        try {
            struct = signer.addSession(sessionAddress, mSpendingLimit, expiryHeight );
        } catch (OstError error) {
            return postErrorInterrupt(error);
        }


        // Removed this: As it is not required any more.
        // .setDataDefination(OstDeviceManagerOperation.KIND_TYPE.AUTHORIZE_SESSION.toUpperCase())

        Map<String, Object> map = new OstPayloadBuilder()
                .setRawCalldata(new GnosisSafe().getAuthorizeSessionData(sessionAddress, mSpendingLimit, expiryHeight))
                .setCallData(new GnosisSafe().getAuthorizeSessionExecutableData(sessionAddress, mSpendingLimit, expiryHeight))
                .setTo( struct.getTokenHolderAddress() )
                .setSignatures( struct.getSignature() )
                .setSigners(Arrays.asList( struct.getSignerAddress() ))
                .setNonce( struct.getNonce() )
                .build();

        JSONObject responseObject = null;
        try {
            responseObject = ostApiClient.postAddSession(map);
            Log.i(TAG, String.format("Response %s", responseObject.toString()));
        } catch (IOException e) {
            Log.e(TAG, "Exception");
            return postErrorInterrupt("wf_as_pr_as_3", OstErrors.ErrorCode.ADD_DEVICE_API_FAILED);
        }
        if (!isValidResponse(responseObject)) {
            return postErrorInterrupt("wf_as_pr_as_4", OstErrors.ErrorCode.ADD_DEVICE_API_FAILED);
        }
        //Request Acknowledge
        postRequestAcknowledge(new OstWorkflowContext(getWorkflowType()),
                new OstContextEntity(OstSession.getById(sessionAddress), OstSdk.SESSION));

        //increment nonce
        OstDeviceManager.getById(ostUser.getDeviceManagerAddress()).incrementNonce();

        Log.i(TAG, "Starting Session polling service");
        Log.i(TAG, "Waiting for update");
        Bundle bundle = OstSessionPollingService.startPolling(mUserId, sessionAddress, OstSession.CONST_STATUS.AUTHORISED,
                OstSession.CONST_STATUS.CREATED);
        if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
            Log.d(TAG, String.format("Polling time out for session Id: %s", sessionAddress));
            return postErrorInterrupt("wf_as_pr_as_4", OstErrors.ErrorCode.POLLING_TIMEOUT);
        }

        Log.i(TAG, "Syncing Entity: Sessions");
        new OstSdkSync(mUserId,OstSdkSync.SYNC_ENTITY.SESSION).perform();

        Log.i(TAG, "Response received for Add session");
        return postFlowComplete();
    }

    private String getCurrentBlockNumber(OstApiClient ostApiClient) {
        String blockNumber = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = ostApiClient.getCurrentBlockNumber();
        } catch (IOException e) {
            Log.e(TAG, "IOException");
        }
        blockNumber = parseResponseForKey(jsonObject, OstConstants.BLOCK_HEIGHT);
        return blockNumber;
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.ADD_SESSION;
    }
}