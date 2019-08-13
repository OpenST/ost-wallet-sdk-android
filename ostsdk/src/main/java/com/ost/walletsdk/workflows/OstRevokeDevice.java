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

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.OstMultiSigSigner;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedRevokeDeviceStruct;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstDeviceManager;
import com.ost.walletsdk.models.entities.OstDeviceManagerOperation;
import com.ost.walletsdk.network.OstApiClient;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.utils.OstPayloadBuilder;
import com.ost.walletsdk.workflows.OstWorkflowContext.WORKFLOW_TYPE;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.walletsdk.workflows.services.OstDevicePollingService;
import com.ost.walletsdk.workflows.services.OstPollingService;

import org.json.JSONObject;
import org.web3j.crypto.WalletUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * It revokes provided device address from device manager.
 * Current device should be in {@link OstDevice.CONST_STATUS#AUTHORIZED} state.
 */
public class OstRevokeDevice extends OstBaseWorkFlow {

    private static final String TAG = "OstRevokeDeviceWithQR";
    private final String mDeviceToBeRevoked;

    public OstRevokeDevice(String userId, String deviceAddress, OstWorkFlowCallback callback) {
        super(userId, callback);
        mDeviceToBeRevoked = deviceAddress;
    }

    @Override
    AsyncStatus performOnAuthenticated() {
        mOstApiClient.getDeviceManager();

        OstDevice ostDeviceToBeRevoked = OstDevice.getById(mDeviceToBeRevoked);
        if (null == ostDeviceToBeRevoked) {
            throw new OstError("wf_rd_pr_6", ErrorCode.INVALID_REVOKE_DEVICE_ADDRESS);
        }
        String prevOwner = ostDeviceToBeRevoked.getLinkedAddress();
        OstMultiSigSigner ostMultiSigSigner = new OstMultiSigSigner(mUserId);
        SignedRevokeDeviceStruct signedData = ostMultiSigSigner.revokeDevice(mDeviceToBeRevoked, prevOwner);

        Log.i(TAG, "Api Call payload");
        AsyncStatus apiCallStatus = makeRevokeDeviceApiCall(signedData);

        if (!apiCallStatus.isSuccess()) {
            //makeRevokeDeviceApiCall [postRevokeDevice] will throw OstApiError. So, this is hypothetical case.
            return postErrorInterrupt("wf_rd_pr_4", ErrorCode.SDK_ERROR);
        }

        //request acknowledge
        postRequestAcknowledge(new OstWorkflowContext(getWorkflowType()),
                new OstContextEntity(OstDevice.getById(mDeviceToBeRevoked), OstSdk.DEVICE));


        return pollForStatus();
    }

    private AsyncStatus makeRevokeDeviceApiCall(SignedRevokeDeviceStruct signedData) {
        Log.i(TAG, "Api Call payload");
        String deviceManagerAddress = signedData.getDeviceManagerAddress();
        Map<String, Object> map = new OstPayloadBuilder()
                .setDataDefination(OstDeviceManagerOperation.KIND_TYPE.REVOKE_DEVICE.toUpperCase())
                .setRawCalldata(signedData.getRawCallData())
                .setCallData(signedData.getCallData())
                .setTo(deviceManagerAddress)
                .setSignatures(signedData.getSignature())
                .setSigners(Arrays.asList(signedData.getSignerAddress()))
                .setNonce(String.valueOf(signedData.getNonce()))
                .build();
        OstApiClient ostApiClient = new OstApiClient(mUserId);
        JSONObject jsonObject = ostApiClient.postRevokeDevice(map);
        Log.d(TAG, String.format("JSON Object response: %s", jsonObject.toString()));
        if (isValidResponse(jsonObject)) {

            //increment nonce
            OstDeviceManager.getById(deviceManagerAddress).incrementNonce();

            return new AsyncStatus(true);
        } else {
            return new AsyncStatus(false);
        }
    }

    private AsyncStatus pollForStatus() {
        Log.i(TAG, "Waiting for update");
        Bundle bundle = OstDevicePollingService.startPolling(mUserId, mDeviceToBeRevoked, OstDevice.CONST_STATUS.REVOKED,
                OstDevice.CONST_STATUS.AUTHORIZED);
        if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
            Log.d(TAG, String.format("Polling time out for device Id: %s", mDeviceToBeRevoked));
            return postErrorInterrupt("wf_rd_pr_5", ErrorCode.POLLING_TIMEOUT);
        }

        Log.i(TAG, "Response received for Add device");
        return postFlowComplete(
                new OstContextEntity(OstDevice.getById(mDeviceToBeRevoked), OstSdk.DEVICE)
        );
    }

    @Override
    void ensureValidParams() {
        if (TextUtils.isEmpty(mDeviceToBeRevoked) || !WalletUtils.isValidAddress(mDeviceToBeRevoked)) {
            throw new OstError("wf_rd_evp_1", ErrorCode.INVALID_DEVICE_ADDRESS);
        }

        super.ensureValidParams();
    }

    @Override
    protected AsyncStatus onUserDeviceValidationPerformed(Object stateObject) {
        //Validate mDeviceAddressToBeAdded
        OstDevice ostDevice = OstDevice.getById(mDeviceToBeRevoked);
        if (null == ostDevice) {
            mOstApiClient.getDevice(mDeviceToBeRevoked);
        }
        ostDevice = OstDevice.getById(mDeviceToBeRevoked);
        if ( null == ostDevice || !ostDevice.canBeRevoked() ) {
            return postErrorInterrupt(new OstError("wf_rd_oudvp_1", ErrorCode.DEVICE_CAN_NOT_BE_REVOKED));
        }
        return super.onUserDeviceValidationPerformed(stateObject);
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return WORKFLOW_TYPE.REVOKE_DEVICE;
    }

    static class RevokeDeviceDataDefinitionInstance extends OstDeviceDataDefinitionInstance {
        private static final String TAG = "RevokeDeviceDDInstance";

        public RevokeDeviceDataDefinitionInstance(JSONObject dataObject, String userId, OstWorkFlowCallback callback) {
            super(dataObject, userId, callback);
        }

        @Override
        public void startDataDefinitionFlow() {
            String deviceAddress = getDeviceAddress();
            OstRevokeDevice ostRevokeDevice = new OstRevokeDevice(userId, deviceAddress, callback);
            ostRevokeDevice.perform();
        }

        @Override
        public void validateApiDependentParams() {
            String deviceAddress = dataObject.optString(OstConstants.QR_DEVICE_ADDRESS);
            new OstApiClient(userId).getDevice(deviceAddress);

            if (null == OstDevice.getById(deviceAddress)) {
                throw new OstError("wf_pe_rd_4", ErrorCode.DEVICE_CAN_NOT_BE_REVOKED);
            }
            if (!OstDevice.getById(deviceAddress).canBeRevoked()) {
                throw new OstError("wf_pe_rd_5", ErrorCode.DEVICE_CAN_NOT_BE_REVOKED);
            }
        }

        @Override
        public WORKFLOW_TYPE getWorkFlowType() {
            return WORKFLOW_TYPE.REVOKE_DEVICE;
        }
    }
}