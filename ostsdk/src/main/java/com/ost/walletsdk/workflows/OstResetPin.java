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

import com.ost.walletsdk.annotations.Nullable;
import android.util.Log;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.OstRecoveryManager;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedResetRecoveryStruct;
import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.models.entities.OstRecoveryOwner;
import com.ost.walletsdk.network.polling.OstResetPinPollingHelper;
import com.ost.walletsdk.network.polling.interfaces.OstPollingCallback;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * It will change current passPhrase recoveryAddress with new passPhrase recoveryAddress.
 */
public class OstResetPin extends OstBaseWorkFlow implements OstPollingCallback {

    private static final String TAG = "OstResetPin";

    private static final String NEW_RECOVERY_OWNER_ADDRESS = "new_recovery_owner_address";
    private static final String TO = "to";
    private static final String SIGNER = "signer";
    private static final String SIGNATURE = "signature";

    private final UserPassphrase currentPassphrase;
    private final UserPassphrase newPassphrase;

    public OstResetPin(String userId, UserPassphrase currentPassphrase, UserPassphrase newPassphrase, OstWorkFlowCallback workFlowCallback) {
        super(userId, workFlowCallback);
        this.currentPassphrase = currentPassphrase;
        this.newPassphrase = newPassphrase;
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.RESET_PIN;
    }

    @Override
    void ensureValidParams() {
        super.ensureValidParams();
        if (null == currentPassphrase) {
            throw new OstError("wf_rp_evp_1", OstErrors.ErrorCode.INVALID_USER_PASSPHRASE);
        }
        if (null == newPassphrase) {
            throw new OstError("wf_rp_evp_2", OstErrors.ErrorCode.INVALID_NEW_USER_PASSPHRASE);
        }
    }

    @Override
    boolean shouldCheckCurrentDeviceAuthorization() {
        return false;
    }

    @Override
    protected boolean shouldAskForAuthentication() {
        return false;
    }

    @Override
    protected AsyncStatus onUserDeviceValidationPerformed(Object stateObject) {
        String newRecoveryOwnerAddress = "";
        mOstApiClient.getDevice(mOstUser.getCurrentDevice().getAddress());

        SignedResetRecoveryStruct struct;
        OstRecoveryManager rkm;
        try {
            rkm = new OstRecoveryManager(mUserId);
            struct = rkm.getResetRecoveryOwnerSignature(currentPassphrase, newPassphrase);
            rkm = null;
        } catch (OstError error) {
            return postErrorInterrupt(error);
        }

        newRecoveryOwnerAddress = struct.getNewRecoveryOwnerAddress();
        Map<String, Object> requestMap = buildApiRequest(newRecoveryOwnerAddress,
                struct.getRecoveryOwnerAddress(), struct.getRecoveryContractAddress(), struct.getSignature());

        JSONObject postRecoveryAddresssResponse = mOstApiClient.postRecoveryOwners(requestMap);
        JSONObject jsonData = postRecoveryAddresssResponse.optJSONObject(OstConstants.RESPONSE_DATA);
        JSONObject resultTypeObject = jsonData.optJSONObject(jsonData.optString(OstConstants.RESULT_TYPE));
        OstRecoveryOwner ostRecoveryOwner = null;
        try {
            ostRecoveryOwner = OstRecoveryOwner.parse(resultTypeObject);
        } catch (JSONException e) {
            return postErrorInterrupt("wf_rp_udv_1", OstErrors.ErrorCode.INVALID_API_RESPONSE);
        }

        postRequestAcknowledge(getWorkflowContext(), new OstContextEntity(ostRecoveryOwner, OstSdk.RECOVERY_OWNER));

        Log.i(TAG, "start reset pin polling");
        new OstResetPinPollingHelper(ostRecoveryOwner.getAddress(), mUserId, this);

        return new AsyncStatus(true);
    }

    private Map<String, Object> buildApiRequest(String newRecoveryOwnerAddress, String recoveryOwnerAddress,
                                                String recoveryContractAddress, String signature) {
        Map<String, Object> map = new HashMap<>();
        map.put(NEW_RECOVERY_OWNER_ADDRESS, newRecoveryOwnerAddress);
        map.put(TO, recoveryContractAddress);
        map.put(SIGNER, recoveryOwnerAddress);
        map.put(SIGNATURE, signature);
        return map;
    }

    @Override
    public void onOstPollingSuccess(@Nullable OstBaseEntity entity, @Nullable JSONObject data) {
        Log.i(TAG, "Recovery Owner with Id " + entity.getId() + " authorized successfully");
        postFlowComplete( new OstContextEntity(entity, OstSdk.RECOVERY_OWNER));
        goToState(WorkflowStateManager.COMPLETED);
    }

    @Override
    public void onOstPollingFailed(OstError error) {
        Log.e(TAG, "Reset Pin FAILED to authorize recovery owner");
        postErrorInterrupt( error );
        goToState(WorkflowStateManager.COMPLETED_WITH_ERROR);
    }
}