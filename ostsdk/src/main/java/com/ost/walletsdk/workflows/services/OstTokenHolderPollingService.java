/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.workflows.services;

import android.os.Bundle;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.models.entities.OstTokenHolder;
import com.ost.walletsdk.network.OstApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OstTokenHolderPollingService  extends OstPollingService {

    private static final String TAG = "OstTokenHolderPollingService";

    /**
     * Polls OstTokenHolder entity periodically.
     * It polls on the caller's thread synchronously.
     *
     * @param userId        user id of user to which token holder is associated
     * @param tokenHolderId Token holder id
     * @param successStatus Success status to poll
     * @param failureStatus Failure status to report
     * @return Bundle hash map having response
     */
    public static Bundle startPolling(String userId,
                                      String tokenHolderId,
                                      String successStatus,
                                      String failureStatus) {
        OstTokenHolderPollingService ostTokenHolderPollingService = new OstTokenHolderPollingService(
                userId,
                tokenHolderId,
                successStatus,
                failureStatus
        );

        return ostTokenHolderPollingService.waitForUpdate();
    }

    private OstTokenHolderPollingService(String userId,
                                         String tokenHolderId,
                                         String successStatus,
                                         String failedStatus) {
        super(
                userId,
                tokenHolderId,
                successStatus,
                failedStatus
        );
    }

    @Override
    protected OstBaseEntity parseEntity(JSONObject entityObject) throws JSONException {
        return OstTokenHolder.parse(entityObject);
    }

    @Override
    protected String getEntityName() {
        return OstSdk.TOKEN_HOLDER;
    }

    @Override
    protected JSONObject poll(String userId, String entityId) throws IOException {
        return new OstApiClient(userId).getTokenHolder();
    }

    @Override
    protected boolean validateParams(String entityId, String successStatus, String failureStatus) {
        return null != OstTokenHolder.getById( entityId ) &&
                OstTokenHolder.isValidStatus( successStatus ) &&
                OstTokenHolder.isValidStatus( failureStatus );
    }
}
