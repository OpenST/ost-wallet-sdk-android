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

import android.util.Log;

import com.ost.walletsdk.ecKeyInteracts.OstMultiSigSigner;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedLogoutSessionsStruct;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.utils.OstPayloadBuilder;
import com.ost.walletsdk.utils.TokenHolder;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class OstLogoutAllSessions extends OstBaseUserAuthenticatorWorkflow {

    private static final String TAG = "OstLogoutAllSessions";

    public OstLogoutAllSessions(String userId, OstWorkFlowCallback callback) {
        super(userId, callback);
    }

    @Override
    AsyncStatus performOnAuthenticated() {

        try {
            mOstApiClient.getDeviceManager();
        } catch (IOException e) {
            Log.e(TAG, "IO Exception ");
        }

        OstMultiSigSigner sigSigner = new OstMultiSigSigner(mUserId);
        SignedLogoutSessionsStruct logoutSessionsStruct = sigSigner.logoutAllSessions();

        Map<String, Object> map = new OstPayloadBuilder()
                .setRawCalldata(
                        new TokenHolder().getLogoutData()
                )
                .setCallData(
                        new TokenHolder().getLogoutExecutableData()
                )
                .setTo( logoutSessionsStruct.getTokenHolderAddress() )
                .setSignatures( logoutSessionsStruct.getSignature() )
                .setSigners(Arrays.asList( logoutSessionsStruct.getSignerAddress() ))
                .setNonce( logoutSessionsStruct.getNonce() )
                .build();

        JSONObject responseObject = null;
        try {
            responseObject = mOstApiClient.postLogoutAllSessions(map);
            Log.i(TAG, String.format("Response %s", responseObject.toString()));
        } catch (IOException e) {
            Log.e(TAG, "Exception");
            return postErrorInterrupt("wf_loas_pr_3", OstErrors.ErrorCode.ADD_DEVICE_API_FAILED);
        }
        if (!isValidResponse(responseObject)) {
            return postErrorInterrupt("wf_loas_pr_4", OstErrors.ErrorCode.ADD_DEVICE_API_FAILED);
        }
        //Request Acknowledge
//        postRequestAcknowledge(new OstWorkflowContext(getWorkflowType()),
//                new OstContextEntity(OstSession.getById(sessionAddress), OstSdk.SESSION));
//
//        //increment nonce
//        OstDeviceManager.getById(ostUser.getDeviceManagerAddress()).incrementNonce();

        return super.performOnAuthenticated();

    }
}
