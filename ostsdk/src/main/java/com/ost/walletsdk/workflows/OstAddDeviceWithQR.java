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
import android.text.TextUtils;
import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.OstMultiSigSigner;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedAddDeviceStruct;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.network.OstApiClient;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.workflows.OstWorkflowContext.WORKFLOW_TYPE;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.walletsdk.workflows.services.OstDevicePollingService;
import com.ost.walletsdk.workflows.services.OstPollingService;

import org.json.JSONObject;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;

/**
 * It authorize device address by adding it to Device Manager.
 * Device to add should be in {@link OstDevice.CONST_STATUS#REGISTERED} state.
 */
public class OstAddDeviceWithQR extends OstBaseWorkFlow {

    private static final String TAG = "OstAddDeviceWithQR";
    private final String mDeviceAddressToBeAdded;

    public OstAddDeviceWithQR(String userId, String deviceAddress, OstWorkFlowCallback callback) {
        super(userId, callback);
        mDeviceAddressToBeAdded = deviceAddress;
    }

    @Override
    AsyncStatus performOnAuthenticated() {
        mOstApiClient.getDeviceManager();

        OstMultiSigSigner ostMultiSigSigner = new OstMultiSigSigner(mUserId);
        SignedAddDeviceStruct signedData = ostMultiSigSigner.addExternalDevice(mDeviceAddressToBeAdded);

        Log.i(TAG, "Api Call payload");
        AsyncStatus apiCallStatus = makeAddDeviceCall(signedData);

        if (!apiCallStatus.isSuccess()) {
            return postErrorInterrupt("wf_adwq_pr_4", ErrorCode.ADD_DEVICE_API_FAILED);
        }

        //request acknowledge
        postRequestAcknowledge(new OstWorkflowContext(getWorkflowType()),
                new OstContextEntity(OstDevice.getById(mDeviceAddressToBeAdded), OstSdk.DEVICE));


        return pollForStatus();
    }

    private AsyncStatus pollForStatus() {
        Log.i(TAG, "Waiting for update");
        Bundle bundle = OstDevicePollingService.startPolling(mUserId, mDeviceAddressToBeAdded, OstDevice.CONST_STATUS.AUTHORIZED,
                OstDevice.CONST_STATUS.CREATED);
        if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
            Log.d(TAG, String.format("Polling time out for device Id: %s", mDeviceAddressToBeAdded));
            return postErrorInterrupt("wf_adwq_pr_5", ErrorCode.POLLING_TIMEOUT);
        }

        Log.i(TAG, "Response received for Add device");
        return postFlowComplete(
                new OstContextEntity(OstDevice.getById(mDeviceAddressToBeAdded), OstSdk.DEVICE)
        );
    }

    @Override
    void ensureValidParams() {
        if ( TextUtils.isEmpty(mDeviceAddressToBeAdded) || !WalletUtils.isValidAddress(mDeviceAddressToBeAdded) ) {
            throw new OstError("wf_ad_evp_1", ErrorCode.INVALID_WORKFLOW_PARAMS);
        }

        super.ensureValidParams();
    }

    @Override
    protected AsyncStatus onUserDeviceValidationPerformed(Object stateObject) {
        //Validate mDeviceAddressToBeAdded
        OstDevice ostDevice = OstDevice.getById(mDeviceAddressToBeAdded);
        if (null == ostDevice) {
            mOstApiClient.getDevice(mDeviceAddressToBeAdded);
        }
        ostDevice = OstDevice.getById(mDeviceAddressToBeAdded);
        if ( null == ostDevice || !ostDevice.canBeAuthorized() ) {
            return postErrorInterrupt(new OstError("wf_adqr_oudvp_1", ErrorCode.DEVICE_CAN_NOT_BE_AUTHORIZED));
        }
        return super.onUserDeviceValidationPerformed(stateObject);
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_DEVICE_WITH_QR_CODE;
    }

    static class AddDeviceDataDefinitionInstance extends OstDeviceDataDefinitionInstance {
        private static final String TAG = "AddDeviceDDInstance";

        public AddDeviceDataDefinitionInstance(JSONObject dataObject, String userId, OstWorkFlowCallback callback) {
            super(dataObject, userId, callback);
        }

        @Override
        public void startDataDefinitionFlow() {
            String deviceAddress = getDeviceAddress();
            OstAddDeviceWithQR ostAddDeviceWithQR = new OstAddDeviceWithQR(userId, deviceAddress, callback);
            ostAddDeviceWithQR.perform();
        }

        @Override
        public void validateApiDependentParams() {
            String deviceAddress = getDeviceAddress();
            new OstApiClient(userId).getDevice(deviceAddress);
            OstDevice ostDevice = OstDevice.getById(deviceAddress);
            if (null == ostDevice) {
                throw new OstError("wf_pe_ad_4", ErrorCode.DEVICE_CAN_NOT_BE_AUTHORIZED);
            }
            if (!ostDevice.canBeAuthorized()) {
                throw new OstError("wf_pe_ad_5", ErrorCode.DEVICE_CAN_NOT_BE_AUTHORIZED);
            }
        }

        @Override
        public WORKFLOW_TYPE getWorkFlowType() {
            return WORKFLOW_TYPE.AUTHORIZE_DEVICE_WITH_QR_CODE;
        }
    }
}