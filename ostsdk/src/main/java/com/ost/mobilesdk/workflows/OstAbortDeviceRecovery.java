/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.workflows;

import android.os.Bundle;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.ecKeyInteracts.OstRecoveryManager;
import com.ost.mobilesdk.ecKeyInteracts.UserPassphrase;
import com.ost.mobilesdk.ecKeyInteracts.structs.SignedRecoverOperationStruct;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.CommonUtils;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstDevicePollingService;
import com.ost.mobilesdk.workflows.services.OstPollingService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OstAbortDeviceRecovery extends OstBaseUserAuthenticatorWorkflow {

    private static final String TAG = "OstAbortDeviceRecovery";
    private SignedRecoverOperationStruct dataHolder;
    private final UserPassphrase passphrase;
    private String mRevokingDeviceAddress;
    private String mRecoveringDeviceAddress;


    public OstAbortDeviceRecovery(String userId, UserPassphrase passphrase, OstWorkFlowCallback callback) {
        super(userId, callback);
        this.passphrase = passphrase;
    }

    @Override
    protected boolean shouldAskForAuthentication() {
        /**
         * Workflow that have UserPassphrase as input shall not ask for pin again.
         */
        return false;
    }

    @Override
    boolean shouldCheckCurrentDeviceAuthorization() {
        /**
         * The recovery device flow can NOT be called by Authorized device.
         * It can only be called by a registered device.
         */
        return false;
    }

    @Override
    protected AsyncStatus onUserDeviceValidationPerformed(Object stateObject) {

        try {
            //To ensure that there are pending recovery going on
            Log.i(TAG, "Ensure pending Recovery");
            ensurePendingRecovery();

        } catch (Throwable th) {
            OstError error;
            if (th instanceof OstError) {
                error = (OstError) th;
            } else {
                error = new OstError("wf_adr_pr_1", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            }
            passphrase.wipe();
            return postErrorInterrupt(error);
        }
        return super.onUserDeviceValidationPerformed(stateObject);
    }

    private void ensurePendingRecovery() throws IOException, JSONException {
        CommonUtils commonUtils = new CommonUtils();

        JSONObject jsonObject = mOstApiClient.getPendingRecovery();

        JSONArray jsonArray = (JSONArray) commonUtils.parseResponseForResultType(jsonObject);

        if (jsonArray.length() < 2) {
            throw new OstError("wf_adr_pr_2", OstErrors.ErrorCode.NO_PENDING_RECOVERY);
        }

        OstDevice ostDevice1 = OstDevice.parse(jsonArray.getJSONObject(0));
        hasValidStatus(ostDevice1.getStatus());
        OstDevice ostDevice2 = OstDevice.parse(jsonArray.getJSONObject(1));
        hasValidStatus(ostDevice2.getStatus());

        if (OstDevice.CONST_STATUS.REVOKING.equalsIgnoreCase(ostDevice1.getStatus())) {
            mRevokingDeviceAddress = ostDevice1.getAddress();
            mRecoveringDeviceAddress = ostDevice2.getAddress();
        } else {
            mRevokingDeviceAddress = ostDevice2.getAddress();
            mRecoveringDeviceAddress = ostDevice1.getAddress();
        }
    }

    private void hasValidStatus(String status) {
        if (OstDevice.CONST_STATUS.REVOKING.equalsIgnoreCase(status) ||
                OstDevice.CONST_STATUS.RECOVERING.equalsIgnoreCase(status)) {
            return;
        }
        throw new OstError("wf_adr_pr_3", ErrorCode.NO_PENDING_RECOVERY);
    }

    @Override
    AsyncStatus performOnAuthenticated() {
        OstRecoveryManager rm;
        try {
            rm = new OstRecoveryManager(mUserId);
            dataHolder = rm.getAbortDeviceSignature(passphrase, mRecoveringDeviceAddress, mRevokingDeviceAddress);
            rm = null;
            Map<String, Object> postData = buildApiRequest(dataHolder);
            mOstApiClient.postAbortRecovery(postData);
            OstContextEntity contextEntity = new OstContextEntity(mCurrentDevice, OstSdk.DEVICE);
            postRequestAcknowledge(contextEntity);

            return pollForStatus();
        } catch (IOException e) {
            OstError error = new OstError("wf_rdwf_poa_1", ErrorCode.POST_RESET_RECOVERY_API_FAILED);
            return postErrorInterrupt(error);
        } catch (OstError error) {
            return postErrorInterrupt(error);
        } finally {

        }
    }

    private AsyncStatus pollForStatus() {
        Log.i(TAG, "Waiting for update");
        Bundle bundle = OstDevicePollingService.startPolling(mUserId, mRecoveringDeviceAddress, OstDevice.CONST_STATUS.REGISTERED,
                OstDevice.CONST_STATUS.AUTHORIZED);
        if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
            Log.d(TAG, String.format("Polling time out for device Id: %s", mRecoveringDeviceAddress));
            return postErrorInterrupt("wf_rd_pr_5", ErrorCode.POLLING_TIMEOUT);
        }

        Log.i(TAG, "Response received for Add device");
        return postFlowComplete();
    }

    private Map<String, Object> buildApiRequest(SignedRecoverOperationStruct dataHolder) {
        Map<String, Object> map = new HashMap<>();
        map.put("to", dataHolder.getRecoveryContractAddress());
        map.put("verifying_contract", dataHolder.getVerifyingContract());
        map.put("old_linked_address", dataHolder.getPrevOwnerOfDeviceToRecover());
        map.put("old_device_address", dataHolder.getDeviceToRevoke());
        map.put("new_device_address", dataHolder.getDeviceToAuthorize());
        map.put("signature", dataHolder.getSignature());
        map.put("signer", dataHolder.getSignerAddress());
        return map;
    }
}