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

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.OstRecoveryManager;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedRecoverOperationStruct;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * It will authorize the current device address by revoking provided device address.
 * Current device should be in registered state before initiating this work flow.
 */
public class OstRecoverDeviceWorkflow extends OstBaseWorkFlow {

    private SignedRecoverOperationStruct dataHolder;
    private String deviceAddressToRecover;
    private OstDevice deviceToRecover;
    private final UserPassphrase passphrase;


    public OstRecoverDeviceWorkflow(String userId, UserPassphrase passphrase, String deviceAddressToRecover, OstWorkFlowCallback callback) {
        super(userId,callback);
        this.deviceAddressToRecover = deviceAddressToRecover;
        this.passphrase = passphrase;
    }

    @Override
    protected boolean shouldAskForAuthentication() {
        /*
         * Workflow that have UserPassphrase as input shall not ask for pin again.
         */
        return false;
    }

    @Override
    boolean shouldCheckCurrentDeviceAuthorization() {
        /*
         * The recovery device flow can NOT be called by Authorized device.
         * It can only be called by a registered device.
         */
        return false;
    }


    @Override
    void ensureValidParams() {
        try {
            if ( null == deviceAddressToRecover || !WalletUtils.isValidAddress(deviceAddressToRecover ) ) {
                throw new OstError("wf_rdwf_oudvp_1", ErrorCode.INVALID_RECOVER_DEVICE_ADDRESS);
            }
            deviceAddressToRecover = Keys.toChecksumAddress( deviceAddressToRecover );

            super.ensureValidParams();

        } catch (Throwable th) {
            passphrase.wipe();
        }
    }




    @Override
    protected AsyncStatus onUserDeviceValidationPerformed(Object stateObject) {

        try {
            //Make sure current device can be authorized.
            ensureDeviceCanBeAuthorized();

            //Make sure current device is NOT deviceAddressToRecover.
            if ( mCurrentDevice.getAddress().equalsIgnoreCase(deviceAddressToRecover) ) {
                throw new OstError("wf_rdwf_oudvp_1", ErrorCode.INVALID_RECOVER_DEVICE_ADDRESS);
            }

            // Fetch information of device to recover.
            mOstApiClient.getDevice(deviceAddressToRecover);

            // To ignore null pointer exception warning.
            deviceToRecover = OstDevice.getById(deviceAddressToRecover);
            if ( null == deviceToRecover ) {
                throw new OstError("wf_rdwf_oudvp_3", ErrorCode.INVALID_RECOVER_DEVICE_ADDRESS);
            }

            //The deviceAddressToRecover must be an Authorized device.
            if ( !deviceToRecover.isAuthorized() ) {
                throw new OstError("wf_rdwf_oudvp_4", ErrorCode.INVALID_RECOVER_DEVICE_ADDRESS);
            }
        } catch (Throwable th) {
            OstError error;
            if ( th instanceof OstError) {
                error = (OstError) th;
            } else {
                error = new OstError("wf_rdwf_oudvp_5", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            }
            passphrase.wipe();
            return postErrorInterrupt(error);
        }
        return super.onUserDeviceValidationPerformed(stateObject);
    }

    private void ensureDeviceCanBeAuthorized() throws OstError {
        if ( !mCurrentDevice.canBeAuthorized() ) {
            //Lets sync Device Information.
            syncCurrentDevice();

            //Check Again
            if ( !mCurrentDevice.canBeAuthorized() ) {
                throw new OstError("wf_rdwf_edcba_1", ErrorCode.DEVICE_CAN_NOT_BE_AUTHORIZED);
            }
        }
    }

    @Override
    AsyncStatus performOnAuthenticated() {
        OstRecoveryManager rm;
        try {
            rm = new OstRecoveryManager(mUserId);
            dataHolder = rm.getRecoverDeviceSignature(passphrase, deviceAddressToRecover);
            rm = null;
            Map<String, Object> postData = buildApiRequest( dataHolder );
            mOstApiClient.postInitiateRecovery(postData);
            OstContextEntity contextEntity = new OstContextEntity(mCurrentDevice, OstSdk.DEVICE);
            postRequestAcknowledge(contextEntity);
            return postFlowComplete(contextEntity);
        } catch (OstError error ) {
            return postErrorInterrupt( error );
        } finally {

        }
    }

    private Map<String, Object> buildApiRequest(SignedRecoverOperationStruct dataHolder) {
        Map<String, Object> map = new HashMap<>();
        map.put("to", dataHolder.getRecoveryContractAddress());
        map.put("verifying_contract", dataHolder.getVerifyingContract());
        map.put("old_linked_address", dataHolder.getPrevOwnerOfDeviceToRecover() );
        map.put("old_device_address", dataHolder.getDeviceToRevoke());
        map.put("new_device_address", dataHolder.getDeviceToAuthorize());
        map.put("signature", dataHolder.getSignature());
        map.put("signer", dataHolder.getSignerAddress());
        return map;
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.INITIATE_DEVICE_RECOVERY;
    }
}