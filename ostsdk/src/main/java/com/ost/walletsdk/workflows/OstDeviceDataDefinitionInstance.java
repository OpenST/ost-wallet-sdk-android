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

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;
import org.web3j.crypto.Keys;

abstract class OstDeviceDataDefinitionInstance implements OstPerform.DataDefinitionInstance {
    private static final String TAG = "DeviceDDInstance";
    final JSONObject dataObject;
    final String userId;
    final OstWorkFlowCallback callback;

    public OstDeviceDataDefinitionInstance(JSONObject dataObject, String userId, OstWorkFlowCallback callback) {
        this.dataObject = dataObject;
        this.userId = userId;
        this.callback = callback;
    }

    String getDeviceAddress() {
        String address = dataObject.optString(OstConstants.QR_DEVICE_ADDRESS);
        if ( null == address ) {
            return null;
        }
        return Keys.toChecksumAddress(address);
    }

    @Override
    public void validateDataPayload() {
        String address = getDeviceAddress();
//        boolean hasDeviceAddress = dataObject.has(OstConstants.QR_DEVICE_ADDRESS);
        if (null == address) {
            throw new OstError("wf_pe_pr_2", OstErrors.ErrorCode.INVALID_DEVICE_ADDRESS);
        }
    }

    @Override
    public void validateDataParams() {

    }

    @Override
    public OstContextEntity getContextEntity() {
        String deviceAddress = dataObject.optString(OstConstants.QR_DEVICE_ADDRESS);
        OstDevice ostDevice = OstDevice.getById(deviceAddress);
        OstContextEntity contextEntity = new OstContextEntity(ostDevice, OstSdk.DEVICE);
        return contextEntity;
    }

    @Override
    public void startDataDefinitionFlow() {

    }

    @Override
    public void validateApiDependentParams() {

    }
}