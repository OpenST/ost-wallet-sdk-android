/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ecKeyInteracts;

import com.ost.walletsdk.ecKeyInteracts.structs.BaseDeviceManagerOperationStruct;
import com.ost.walletsdk.ecKeyInteracts.structs.OstSignWithMnemonicsStruct;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedAddDeviceStruct;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedAddSessionStruct;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedLogoutSessionsStruct;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedRevokeDeviceStruct;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstDeviceManager;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.utils.EIP712;
import com.ost.walletsdk.utils.GnosisSafe;
import com.ost.walletsdk.utils.TokenHolder;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors.ErrorCode;

import org.json.JSONObject;
import org.web3j.crypto.Keys;

public class OstMultiSigSigner {
    private static final String TAG = "OstMultiSigSigner";
    private final String mUserId;
    public OstMultiSigSigner(String userId) {
        mUserId = userId;
    }

    public SignedAddSessionStruct addSession(String sessionAddress, String spendingLimit, String expiryHeight) throws OstError {
        OstUser user = OstUser.getById(mUserId);
        sessionAddress = Keys.toChecksumAddress(sessionAddress);
        SignedAddSessionStruct struct = new SignedAddSessionStruct(sessionAddress, spendingLimit, expiryHeight);

        //Set Executable Data
        String executableData = new GnosisSafe().getAuthorizeSessionExecutableData(sessionAddress, spendingLimit, expiryHeight);
        struct.setCallData(executableData);

        //Special Case: Change the To Address. TokenHolder is the to address in this case.
        struct.setTokenHolderAddress(user.getTokenHolderAddress());


        //Set Common Data.
        setCommonData(struct);

        //Compute Message Hash
        try {
            String messageHash = new EIP712(struct.getTypedData()).toEIP712TransactionHash();
            struct.setMessageHash(messageHash);
        } catch (Exception e) {
            OstError ostError = new OstError("km_gss_as_5", ErrorCode.FAILED_TO_GENERATE_MESSAGE_HASH);
            throw ostError;
        }

        InternalKeyManager ikm;
        try {
            ikm = new InternalKeyManager(mUserId);

            if (!ikm.canSignWithSession(sessionAddress)) {
                OstError ostError = new OstError("km_gss_as_6", ErrorCode.INVALID_SESSION_ADDRESS);
                throw ostError;
            }

            //Sign the data.
            signData(struct, ikm);

        } catch (Throwable throwable) {
            throw throwable;
        } finally {
            ikm = null;
        }

        //All good
        return struct;
    }

    public SignedLogoutSessionsStruct logoutAllSessions() {
        OstUser user = OstUser.getById(mUserId);
        SignedLogoutSessionsStruct struct = new SignedLogoutSessionsStruct();

        //Set Executable Data
        String executableData = new TokenHolder().getLogoutExecutableData();
        struct.setCallData(executableData);

        String rawCallData = new TokenHolder().getLogoutData();
        struct.setRawCallData(rawCallData);

        //Special Case: Change the To Address. TokenHolder is the to address in this case.
        struct.setTokenHolderAddress(user.getTokenHolderAddress());

        //Set Common Data.
        setCommonData(struct);

        //Compute Message Hash
        try {
            String messageHash = new EIP712(struct.getTypedData()).toEIP712TransactionHash();
            struct.setMessageHash(messageHash);
        } catch (Exception e) {
            OstError ostError = new OstError("km_gss_los_5", ErrorCode.FAILED_TO_GENERATE_MESSAGE_HASH);
            throw ostError;
        }

        InternalKeyManager ikm;
        try {
            ikm = new InternalKeyManager(mUserId);

            //Sign the data.
            signData(struct, ikm);

        } catch (Throwable throwable) {
            throw throwable;
        } finally {
            ikm = null;
        }

        //All good
        return struct;
    }

    public SignedAddDeviceStruct addExternalDevice(String deviceToBeAdded) {
        KeyMetaStruct keyMeta = InternalKeyManager.getKeyMataStruct(mUserId);
        if ( null == keyMeta) {
            throw new OstError("km_gss_adwm_1", ErrorCode.DEVICE_NOT_SETUP);
        }

        // Get current device address.
        String signerAddress = keyMeta.deviceAddress;
        SignedAddDeviceStruct struct = createAddDeviceStruct(deviceToBeAdded);

        InternalKeyManager ikm = null;
        try {
            ikm = new InternalKeyManager(mUserId);
            String signature = ikm.signWithDeviceKey( struct.getMessageHash() );
            struct.setDeviceOwnerAddress(signerAddress);
            struct.setSignature(signature);
            ikm = null;
        }finally {
            ikm = null;
        }
        return struct;
    }

    public SignedAddDeviceStruct addCurrentDeviceWithMnemonics(byte[] mnemonics) {
        KeyMetaStruct keyMeta = InternalKeyManager.getKeyMataStruct(mUserId);
        if ( null == keyMeta) {
            throw new OstError("km_gss_adwm_1", ErrorCode.DEVICE_NOT_SETUP);

        }

        // Get current device address.
        String deviceAddress = keyMeta.deviceAddress;
        SignedAddDeviceStruct struct = createAddDeviceStruct(deviceAddress);

        OstSignWithMnemonicsStruct ostSignWithMnemonicsStruct = new OstSignWithMnemonicsStruct(mnemonics, struct.getMessageHash());

        InternalKeyManager ikm = null;
        try {
            ikm = new InternalKeyManager(mUserId);
            //Sign the data.
            ikm.signWithExternalDevice(ostSignWithMnemonicsStruct);
            String signerAddress =  ostSignWithMnemonicsStruct.getSigner();
            String signature = ostSignWithMnemonicsStruct.getSignature();
            if (null == signature || null == signerAddress ) {
                throw new OstError("km_gss_adwm_2", ErrorCode.FAILED_TO_SIGN_DATA);
            }
            struct.setDeviceOwnerAddress(signerAddress);
            struct.setSignature(signature);
            ikm = null;
        } finally {
            ikm = null;
        }
        return struct;
    }

    private SignedAddDeviceStruct createAddDeviceStruct(String deviceAddressToBeAdded) {
        OstDevice device = OstDevice.getById(deviceAddressToBeAdded);
        if ( null == device ) {
            throw new OstError("km_gss_cads_1", ErrorCode.INSUFFICIENT_DATA);
        }

        if ( device.isAuthorized() ) {
            throw new OstError("km_gss_cads_2", ErrorCode.DEVICE_ALREADY_AUTHORIZED);
        }

        if ( !device.canBeAuthorized() ) {
            throw new OstError("km_gss_cads_3", ErrorCode.DEVICE_CAN_NOT_BE_AUTHORIZED);
        }

        SignedAddDeviceStruct struct = new SignedAddDeviceStruct(deviceAddressToBeAdded);
        String callData = new GnosisSafe().getAddOwnerWithThresholdExecutableData(deviceAddressToBeAdded);
        struct.setCallData(callData);

        String rawCallData = new GnosisSafe().getAddOwnerWithThresholdCallData(deviceAddressToBeAdded);
        struct.setRawCallData(rawCallData);
        setCommonData(struct);

        // Generate message hash.
        try {
            String messageHash = new EIP712(struct.getTypedData()).toEIP712TransactionHash();
            struct.setMessageHash(messageHash);
        } catch (Exception e) {
            OstError ostError = new OstError("km_gss_cads_4", ErrorCode.FAILED_TO_GENERATE_MESSAGE_HASH);
            throw ostError;
        }

        return struct;
    }


    //region - common methods. Make sure they are private
    private void setCommonData(BaseDeviceManagerOperationStruct struct) {

        OstUser user = OstUser.getById(mUserId);
        if (null == user) {
            OstError ostError = new OstError("km_gss_as_2", ErrorCode.INSUFFICIENT_DATA);
            throw ostError;
        }

        OstDeviceManager deviceManager = OstDeviceManager.getById(user.getDeviceManagerAddress());
        if ( null == deviceManager ) {
            OstError ostError = new OstError("km_gss_as_3", ErrorCode.INSUFFICIENT_DATA);
            throw ostError;
        }


        //Set device manager address
        struct.setDeviceManagerAddress(deviceManager.getAddress());

        //Set token holder address
        if ( null == struct.getToAddress() ) {
            struct.setToAddress(deviceManager.getAddress());
        }


        //Set nonce
        int nonce = deviceManager.getNonce();
        struct.setNonce( String.valueOf(nonce) );


        JSONObject typedData = new GnosisSafe.SafeTxnBuilder()
                .setCallData(struct.getCallData())
                .setToAddress( struct.getToAddress() )
                .setVerifyingContract(struct.getVerifyingContract())
                .setNonce( struct.getNonce() )
                .build();
        struct.setTypedData( typedData );
    }
    //endregion

    //region - data signers. Make sure they are private.
    private void signData(BaseDeviceManagerOperationStruct struct, InternalKeyManager ikm) {
        String messageHash = struct.getMessageHash();
        //Check if we have a device key.
        KeyMetaStruct keyMeta = InternalKeyManager.getKeyMataStruct(mUserId);
        if ( null == keyMeta) {
            throw new OstError("km_gss_sd_1", ErrorCode.DEVICE_NOT_SETUP);
        }

        try {
            if ( null == ikm ) {
                ikm = new InternalKeyManager(mUserId);
            }
            String signature = ikm.signWithDeviceKey(messageHash);
            struct.setSignature(signature);
            struct.setSignerAddress( keyMeta.deviceAddress );
        } catch (Throwable throwable) {
            ikm = null;
            OstError ostError = new OstError("km_gss_sd_2", ErrorCode.FAILED_TO_SIGN_DATA);
            ostError.setStackTrace( throwable.getStackTrace() );
            throw ostError;
        }
    }

    public SignedRevokeDeviceStruct revokeDevice(String deviceToBeRevoked, String prevOwner) {
        KeyMetaStruct keyMeta = InternalKeyManager.getKeyMataStruct(mUserId);
        if (null == keyMeta) {
            throw new OstError("km_gss_adwm_1", ErrorCode.DEVICE_NOT_SETUP);
        }

        // Get current device address.
        String signerAddress = keyMeta.deviceAddress;
        SignedRevokeDeviceStruct struct = createRevokeDeviceStruct(deviceToBeRevoked, prevOwner);

        InternalKeyManager ikm = null;
        try {
            ikm = new InternalKeyManager(mUserId);
            String signature = ikm.signWithDeviceKey(struct.getMessageHash());
            struct.setDeviceOwnerAddress(signerAddress);
            struct.setSignature(signature);
            ikm = null;
        } finally {
            ikm = null;
        }
        return struct;
    }

    private SignedRevokeDeviceStruct createRevokeDeviceStruct(String deviceToBeRevoked, String prevOwner) {
        OstDevice device = OstDevice.getById(deviceToBeRevoked);
        if (null == device) {
            throw new OstError("km_gss_crds_1", ErrorCode.INSUFFICIENT_DATA);
        }

        if (device.isRevoked()) {
            throw new OstError("km_gss_crds_2", ErrorCode.DEVICE_ALREADY_REVOKED);
        }

        if (!device.canBeRevoked()) {
            throw new OstError("km_gss_crds_3", ErrorCode.DEVICE_CAN_NOT_BE_REVOKED);
        }

        SignedRevokeDeviceStruct struct = new SignedRevokeDeviceStruct(deviceToBeRevoked);
        String callData = new GnosisSafe().getRemoveOwnerWithThresholdExecutableData(prevOwner, deviceToBeRevoked);
        struct.setCallData(callData);

        String rawCallData = new GnosisSafe().getRemoveOwnerWithThresholdCallData(prevOwner, deviceToBeRevoked);
        struct.setRawCallData(rawCallData);
        setCommonData(struct);

        // Generate message hash.
        try {
            String messageHash = new EIP712(struct.getTypedData()).toEIP712TransactionHash();
            struct.setMessageHash(messageHash);
        } catch (Exception e) {
            OstError ostError = new OstError("km_gss_crds_4", ErrorCode.FAILED_TO_GENERATE_MESSAGE_HASH);
            throw ostError;
        }

        return struct;
    }
    //endregion

}
