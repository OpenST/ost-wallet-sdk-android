/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.ecKeyInteracts;

import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiError;
import com.ost.mobilesdk.network.OstHttpRequestClient;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;

public class OstApiSigner implements OstHttpRequestClient.ApiSigner {

    String mUserId;
    public OstApiSigner(String userId) {
        mUserId = userId;
    }

    /**
     * Generates signature for HTTP Api calls (ETH Personal Sign).
     * @param dataToSign - byte[] to sign.
     * @return
     */
    @Override
    public String sign(byte[] dataToSign) {
        InternalKeyManager ikm = null;
        try {
            ikm = new InternalKeyManager(mUserId);
            return ikm.signBytesWithApiSigner(dataToSign);
        } finally {
            ikm = null;
        }
    }

    public void apiSignerUnauthorized(OstApiError error) {
        try {
            if (null == error || !error.isApiSignerUnauthorized()) {
                return;
            }

            KeyMetaStruct meta = InternalKeyManager.getKeyMataStruct(mUserId);
            if (null != meta) {
                String currentDeviceAddress = meta.getDeviceAddress();
                if (null != currentDeviceAddress) {
                    OstDevice device = OstDevice.getById(currentDeviceAddress);
                    if (null != device && device.canBeRegistered()) {
                        //Ignore this call for devices with status Created.
                        return;
                    }
                }
            }
            //Tell ikm about it.
            InternalKeyManager.apiSignerUnauthorized(mUserId);

            //Flush the current device.
            OstUser user = OstUser.getById(mUserId);
            if (null != user) {
                user.flushCurrentDevice();
            }
        } catch (Throwable th) {
            OstError caughtError;
            if ( th instanceof OstError ) {
                caughtError = (OstError) th;
            } else {
                caughtError = new OstError("km_as_asu_1", OstErrors.ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            }
            throw caughtError;
        }
    }

}