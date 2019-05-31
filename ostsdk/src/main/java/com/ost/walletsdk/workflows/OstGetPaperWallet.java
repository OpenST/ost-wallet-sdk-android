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
import com.ost.walletsdk.ecKeyInteracts.OstKeyManager;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

/**
 * It return 12 words mnemonics of the current device key in flowComplete callback.
 * In callback OstContextActivity should be used to get mnemonics as byte array.
 */
public class OstGetPaperWallet extends OstBaseWorkFlow {

    private static final String TAG = "OstGetPaperWallet";

    public OstGetPaperWallet(String userId, OstWorkFlowCallback callback) {
        super(userId, callback);
    }

    @Override
    AsyncStatus performOnAuthenticated() {
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        OstContextEntity ostContextEntity = new OstContextEntity(ostKeyManager.getMnemonics(), OstSdk.MNEMONICS);
        return postFlowComplete(ostContextEntity);
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.GET_DEVICE_MNEMONICS;
    }
}