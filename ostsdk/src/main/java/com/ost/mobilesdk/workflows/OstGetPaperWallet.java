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

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.ecKeyInteracts.OstKeyManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

/**
 * 1. Ask for pin or biometric
 * 2. show 12 words
 */
public class OstGetPaperWallet extends OstBaseUserAuthenticatorWorkflow {

    private static final String TAG = "OstGetPaperWallet";

    public OstGetPaperWallet(String userId, OstWorkFlowCallback callback) {
        super(userId, callback);
    }

    @Override
    AsyncStatus performOnAuthenticated() {
        OstKeyManager ostKeyManager = null;
        try {
            ostKeyManager = new OstKeyManager(mUserId);
            OstContextEntity ostContextEntity = new OstContextEntity(ostKeyManager.getMnemonics(), OstSdk.PAPER_WALLET);
            return postFlowComplete(ostContextEntity);
        } catch (OstError error) {
            return postErrorInterrupt( error );
        } catch (Throwable th) {
            return postErrorInterrupt("wf_ogpw_poa_1", OstErrors.ErrorCode.UNKNOWN);
        } finally {
            ostKeyManager = null;
        }
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.GET_PAPER_WALLET;
    }
}