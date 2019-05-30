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

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.OstMultiSigSigner;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedAddDeviceStruct;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstDeviceManager;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.walletsdk.workflows.services.OstDevicePollingService;
import com.ost.walletsdk.workflows.services.OstPollingService;

import java.io.IOException;

/**
 * It adds current Device using provided mnemonics
 * Current device should be in {@link OstDevice.CONST_STATUS#REGISTERED} state.
 */
public class OstAddCurrentDeviceWithMnemonics extends OstBaseWorkFlow implements OstPinAcceptInterface {

    private static final String TAG = "OstADWithMnemonics";
    private final byte[] mMnemonics;
    SignedAddDeviceStruct signedData;
    String mAddedDeviceAddress;

    public OstAddCurrentDeviceWithMnemonics(String userId, byte[] mnemonics, OstWorkFlowCallback callback) {
        super(userId, callback);
        mMnemonics = mnemonics;
    }


    @Override
    void ensureValidParams() {
        if ( null == mMnemonics || mMnemonics.length < 1) {
            throw new OstError("wf_acdwm_evp_1", OstErrors.ErrorCode.INVALID_WORKFLOW_PARAMS);
        }
        super.ensureValidParams();
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_DEVICE_WITH_MNEMONICS;
    }

    protected AsyncStatus performUserDeviceValidation(Object stateObject) {



        try {
            ensureDeviceManager();
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        }
        return super.performUserDeviceValidation(stateObject);
    }

    @Override
    AsyncStatus performOnAuthenticated() {
        mOstApiClient.getDeviceManager();

        String deviceAddress = mOstUser.getCurrentDevice().getAddress();
        String deviceManagerAddress = OstUser.getById(mUserId).getDeviceManagerAddress();
        OstMultiSigSigner signer = null;
        try {
            signer = new OstMultiSigSigner(mUserId);
            signedData = signer.addCurrentDeviceWithMnemonics(mMnemonics);
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        }

        mAddedDeviceAddress = signedData.getDeviceToBeAdded();

        Log.i(TAG, "Api Call payload");
        AsyncStatus apiCallStatus = makeAddDeviceCall(signedData);

        if ( apiCallStatus.isSuccess() ) {
            //request acknowledge
            postRequestAcknowledge(new OstWorkflowContext(getWorkflowType()),
                    new OstContextEntity(OstDevice.getById(mAddedDeviceAddress), OstSdk.DEVICE));

            //increment nonce
            OstDeviceManager.getById(mOstUser.getDeviceManagerAddress()).incrementNonce();

            //Start the polling
            return startPolling();
        }
        return apiCallStatus;
    }

    AsyncStatus startPolling() {
        Log.i(TAG, "Waiting for update");
        Bundle bundle = OstDevicePollingService.startPolling(mUserId, mAddedDeviceAddress, OstDevice.CONST_STATUS.AUTHORIZED,
                OstDevice.CONST_STATUS.REGISTERED);

        boolean hasTimedout = bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true);
        if ( hasTimedout ) {
            Log.d(TAG, String.format("Polling time out for device Id: %s", mAddedDeviceAddress));
            return postErrorInterrupt("wf_adwm_pr_5", OstErrors.ErrorCode.POLLING_TIMEOUT);
        }

        Log.i(TAG, "Response received for Add device");
        return postFlowComplete(
                new OstContextEntity(OstDevice.getById(mAddedDeviceAddress), OstSdk.DEVICE)
        );
    }

    @Override
    protected boolean shouldCheckCurrentDeviceAuthorization() {
        return false;
    }

}