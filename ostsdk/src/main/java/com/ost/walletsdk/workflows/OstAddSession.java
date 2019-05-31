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
import java.util.Arrays;
import java.util.Map;

/**
 * It adds Session to the user's current device with provided spec
 * like spendingLimit and expiry time in secs.
 * To do any rule execution transaction sessions needs to be added.
 * Session added are specific to device and can't be used from another device.
 */
public class OstAddSession extends OstBaseWorkFlow implements OstPinAcceptInterface {

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
            OstError err = OstError.ApiResponseError("wf_as_pr_1", "getCurrentBlockNumber", null);
            return postErrorInterrupt(err);
        }

        OstUser ostUser = mOstUser;

        String expiryHeight = calculateExpirationHeight(mExpiresAfterInSecs);
        ostApiClient.getDeviceManager();

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

        JSONObject responseObject = ostApiClient.postAddSession(map);
        Log.i(TAG, String.format("Response %s", responseObject.toString()));

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
        return postFlowComplete(
                new OstContextEntity(OstSession.getById(sessionAddress), OstSdk.SESSION)
        );
    }

    private String getCurrentBlockNumber(OstApiClient ostApiClient) {
        String blockNumber = null;
        JSONObject jsonObject = ostApiClient.getCurrentBlockNumber();
        blockNumber = parseResponseForKey(jsonObject, OstConstants.BLOCK_HEIGHT);
        return blockNumber;
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.ADD_SESSION;
    }
}