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

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.models.Impls.OstModelFactory;
import com.ost.walletsdk.models.Impls.OstSessionKeyModelRepository;
import com.ost.walletsdk.network.OstApiError;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors.ErrorCode;

import java.util.List;

public class OstKeyManager {
    private static final String TAG = "OstKeyManager";


    private final String mUserId;
    private KeyMetaStruct mKeyMetaStruct;

    public OstKeyManager(String userId) {
        this(userId,false);
    }

    public OstKeyManager(String userId, boolean createDeviceIfNeeded) {
        this.mUserId = userId;
        mKeyMetaStruct = InternalKeyManager.getKeyMataStruct(userId);
        if (null == mKeyMetaStruct && createDeviceIfNeeded ) {

            //Create new KeyManagerInstance so that keys are created.
            InternalKeyManager ikm = new InternalKeyManager(userId);

            //Fetch KeyMataStruct.
            mKeyMetaStruct = InternalKeyManager.getKeyMataStruct(userId);
            ikm = null;
        }
    }

    public String getApiKeyAddress() {
        if ( null == mKeyMetaStruct ) {
            return null;
        }
        return mKeyMetaStruct.getApiAddress();
    }

    public byte[] getMnemonics() {
        InternalKeyManager ikm = new InternalKeyManager(mUserId);

        String deviceAddress = getDeviceAddress();
        byte[] mnemonics = ikm.getMnemonics(deviceAddress);
        ikm = null;
        return mnemonics;
    }

    public String createSessionKey() {
        InternalKeyManager ikm = new InternalKeyManager(mUserId);
        String address = ikm.createSessionKey();
        ikm = null;

        if ( null == address ) {
            throw new OstError("km_okm_csk_1", ErrorCode.FAILED_TO_GENERATE_ETH_KEY);
        }

        return address;
    }

    public String getDeviceAddress() {
        if ( null == mKeyMetaStruct ) {
            return null;
        }
        return mKeyMetaStruct.getDeviceAddress();
    }

    public void handleSessionApiError(OstApiError ostApiError, String sessionAddress) {
        List<OstApiError.ApiErrorData> errorData = ostApiError.getErrorData();
        if (ostApiError.isNotFound()
                && 0 < errorData.size()
                && OstConstants.SESSION_ADDRESS.equalsIgnoreCase(
                errorData.get(0).getParameter()
        )) {
            // wipe session key which is not available in backend
            wipeSession(sessionAddress);
        }
    }

    public boolean isBiometricEnabled() {
        if (null == mKeyMetaStruct) {
            return false;
        }
        return mKeyMetaStruct.isBiometricEnabled();
    }

    private void wipeSession(String address) {
        OstModelFactory.getSessionModel().deleteEntity(address);
        new OstSessionKeyModelRepository().deleteSessionKey(address);
    }
}