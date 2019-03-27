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

import android.os.Bundle;
import android.util.Log;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.OstTransactionSigner;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedTransactionStruct;
import com.ost.walletsdk.models.entities.OstRule;
import com.ost.walletsdk.models.entities.OstTransaction;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.network.OstApiError;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.utils.CommonUtils;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.walletsdk.workflows.services.OstPollingService;
import com.ost.walletsdk.workflows.services.OstTransactionPollingService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It executes rule transaction.
 * Before execute transactions make sure you have created Session, having sufficient spending limit
 * and within the expiry limit,
 * You can create session by
 * {@link OstSdk#addSession(String, String, long, OstWorkFlowCallback)} every time you need sessions and
 * {@link OstSdk#activateUser(UserPassphrase, long, String, OstWorkFlowCallback)} once you activate user.
 * Rule should be passed to execute rule.
 * {@link OstSdk#RULE_NAME_DIRECT_TRANSFER#RULE_NAME_PRICER}
 * It can do multiple transfers by passing list of token holder receiver addresses with
 * respective amounts.
 */
public class OstExecuteTransaction extends OstBaseUserAuthenticatorWorkflow {

    private static final String TAG = "OstExecuteTransaction";
    private final List<String> mTokenHolderAddresses;
    private final List<String> mAmounts;
    private final String mRuleName;
    private final Map<String, Object> mMeta;

    public OstExecuteTransaction(String userId,
                                 List<String> tokenHolderAddresses,
                                 List<String> amounts,
                                 String ruleName,
                                 Map<String, Object> meta,
                                 OstWorkFlowCallback callback) {
        super(userId, callback);
        mTokenHolderAddresses = tokenHolderAddresses;
        mAmounts = amounts;
        mRuleName = ruleName;
        mMeta = meta;
    }


    @Override
    AsyncStatus performOnAuthenticated() {

        OstTransactionSigner ostTransactionSigner = new OstTransactionSigner(mUserId);
        SignedTransactionStruct signedTransactionStruct = ostTransactionSigner
                .getSignedTransaction(mRuleName, mTokenHolderAddresses, mAmounts, getRuleAddressFor(mRuleName));

        Log.i(TAG, "Building transaction request");
        Map<String, Object> map = buildTransactionRequest(signedTransactionStruct);

        Log.i(TAG, "post transaction execute api");
        String entityId = null;
        try {
            entityId = postTransactionApi(map);
        } catch (OstApiError ostApiError) {
            new OstSdkSync(mUserId, OstSdkSync.SYNC_ENTITY.SESSION).perform();
            throw ostApiError;
        }
        if ( null == entityId ) {
            return postErrorInterrupt("wf_et_pr_4", OstErrors.ErrorCode.TRANSACTION_API_FAILED);
        }

        Log.i(TAG, "Increment nonce");
        //Increment Nonce
        signedTransactionStruct.getSession().incrementNonce();

        //Request Acknowledge
        postRequestAcknowledge(new OstWorkflowContext(getWorkflowType()),
                new OstContextEntity(OstTransaction.getById(entityId), OstSdk.TRANSACTION));

        Log.i(TAG, "start polling");
        Bundle bundle = OstTransactionPollingService.startPolling(mUserId, entityId,
                OstTransaction.CONST_STATUS.SUCCESS, OstTransaction.CONST_STATUS.FAILED);
        if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
            return postErrorInterrupt("wf_et_pr_5", OstErrors.ErrorCode.POLLING_TIMEOUT);
        }
        if (!bundle.getBoolean(OstPollingService.EXTRA_IS_VALID_RESPONSE, false)) {
            Log.i(TAG, "Not a valid response retrying again");
            try {
                mOstApiClient.getSession(signedTransactionStruct.getSignerAddress());
            } catch (IOException e) {
                Log.e(TAG, "update sessions error", e);
            }
            return postErrorInterrupt("wf_et_pr_6", OstErrors.ErrorCode.TRANSACTION_API_FAILED);
        } else {
            return postFlowComplete(
                    new OstContextEntity(OstTransaction.getById(entityId), OstSdk.TRANSACTION)
            );
        }
    }

    private String getRuleAddressFor(String ruleName) {
        OstRule[] ostRules = mOstRules;
        for (int i = 0; i < ostRules.length; i++) {
            if (ruleName.equalsIgnoreCase(ostRules[i].getName())) {
                return ostRules[i].getAddress();
            }
        }
        throw new OstError("wf_et_graf_1", OstErrors.ErrorCode.RULE_NOT_FOUND);
    }

    @Override
    protected boolean shouldAskForAuthentication() {
        return false;
    }

    @Override
    protected AsyncStatus onUserDeviceValidationPerformed(Object stateObject) {
        try {
            ensureOstRules( mRuleName );
        } catch (OstError error) {
            return postErrorInterrupt(error);
        }
        return super.onUserDeviceValidationPerformed(stateObject);
    }


    private String postTransactionApi(Map<String, Object> map) {
        JSONObject jsonObject = null;
        try {
            jsonObject = mOstApiClient.postExecuteTransaction(map);
        } catch (IOException e) {
            Log.e(TAG, "IO exception in post Transaction");
            return null;
        }
        if (isValidResponse(jsonObject)) {
            return parseResponseForKey(jsonObject, OstTransaction.ID);
        } else {
            return null;
        }
    }

    private Map<String, Object> buildTransactionRequest(SignedTransactionStruct signedTransactionStruct) {

        return new ExecuteRuleRequestBuilder()
                .setToAddress(signedTransactionStruct.getTokenHolderContractAddress())
                .setCallData(signedTransactionStruct.getCallData())
                .setNonce(signedTransactionStruct.getNonce())
                .setRawCallData(signedTransactionStruct.getRawCallData())
                .setSignature(signedTransactionStruct.getSignature())
                .setSigner(signedTransactionStruct.getSignerAddress())
                .setMetaProperty(mMeta)
                .build();
    }


    static class ExecuteRuleRequestBuilder {

        private static final String TO = "to";
        private static final String RAW_CALL_DATA = "raw_calldata";
        private static final String NONCE = "nonce";
        private static final String CALL_DATA = "calldata";
        private static final String SIGNATURE = "signature";
        private static final String SIGNER = "signer";
        private static final String MATA_PROPERTY = "meta_property";
        private String toAddress = "0x0";

        public ExecuteRuleRequestBuilder setToAddress(String toAddress) {
            this.toAddress = toAddress;
            return this;
        }

        public ExecuteRuleRequestBuilder setRawCallData(String rawCallData) {
            this.rawCallData = rawCallData;
            return this;
        }

        public ExecuteRuleRequestBuilder setNonce(String nonce) {
            this.nonce = nonce;
            return this;
        }

        public ExecuteRuleRequestBuilder setCallData(String callData) {
            this.callData = callData;
            return this;
        }

        public ExecuteRuleRequestBuilder setSignature(String signature) {
            this.signature = signature;
            return this;
        }

        public ExecuteRuleRequestBuilder setSigner(String signer) {
            this.signer = signer;
            return this;
        }

        public ExecuteRuleRequestBuilder setMetaProperty(Map<String, Object> metaProperty) {
            this.metaProperty = metaProperty;
            return this;
        }

        private String rawCallData = new String();
        private String nonce = "0";
        private String callData = "0x0";
        private String signature = "0x0";
        private String signer = "0x0";
        private Map<String, Object> metaProperty = new HashMap<>();


        ExecuteRuleRequestBuilder() {
        }

        Map<String, Object> build() {
            Map<String, Object> map = new HashMap<>();
            map.put(TO, toAddress);
            map.put(RAW_CALL_DATA, rawCallData);
            map.put(NONCE, nonce);
            map.put(CALL_DATA, callData);
            map.put(SIGNATURE, signature);
            map.put(SIGNER, signer);
            map.put(MATA_PROPERTY, metaProperty);
            return map;
        }
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.EXECUTE_TRANSACTION;
    }

    static class TransactionDataDefinitionInstance implements OstPerform.DataDefinitionInstance {
        private static final String TAG = "TransactionDDInstance";

        private final JSONObject dataObject;
        private final String userId;
        private final OstWorkFlowCallback callback;
        private final JSONObject metaObject;

        public TransactionDataDefinitionInstance(JSONObject dataObject,
                                                 JSONObject metaObject,
                                                 String userId,
                                                 OstWorkFlowCallback callback) {
            this.dataObject = dataObject;
            this.userId = userId;
            this.metaObject = metaObject;
            this.callback = callback;
        }

        @Override
        public void validateDataPayload() {
            boolean hasRuleName = dataObject.has(OstConstants.QR_RULE_NAME);
            boolean hasTokenHolderAddresses = dataObject.has(OstConstants.QR_TOKEN_HOLDER_ADDRESSES);
            boolean hasAmounts = dataObject.has(OstConstants.QR_AMOUNTS);
            boolean hasTokenId = dataObject.has(OstConstants.QR_TOKEN_ID);
            if (!(hasRuleName && hasTokenHolderAddresses && hasAmounts && hasTokenId)) {
                throw new OstError("wf_pe_pr_3", OstErrors.ErrorCode.INVALID_QR_TRANSACTION_DATA);
            }
        }

        @Override
        public void validateDataParams() {
            String tokenId = dataObject.optString(OstConstants.QR_TOKEN_ID);
            if (!OstUser.getById(userId).getTokenId().equalsIgnoreCase(tokenId)) {
                throw new OstError("wf_et_pr_1", OstErrors.ErrorCode.INVALID_TOKEN_ID);
            }
        }

        @Override
        public OstContextEntity getContextEntity() {
            JSONObject jsonObject = updateJSONKeys(dataObject);
            OstContextEntity contextEntity = new OstContextEntity(jsonObject, OstSdk.JSON_OBJECT);
            return contextEntity;
        }

        private JSONObject updateJSONKeys(JSONObject dataObject) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(OstConstants.RULE_NAME,
                        dataObject.optString(OstConstants.QR_RULE_NAME));
                jsonObject.put(OstConstants.TOKEN_HOLDER_ADDRESSES,
                        dataObject.optJSONArray(OstConstants.QR_TOKEN_HOLDER_ADDRESSES));
                jsonObject.put(OstConstants.AMOUNTS,
                        dataObject.optJSONArray(OstConstants.QR_AMOUNTS));
                jsonObject.put(OstConstants.TOKEN_ID,
                        dataObject.optJSONArray(OstConstants.QR_TOKEN_ID));
            } catch (JSONException e) {
                Log.e(TAG, "JSON Exception in updateJSONKeys: ", e);
            }
            return jsonObject;
        }

        @Override
        public void startDataDefinitionFlow() {
            String ruleName = dataObject.optString(OstConstants.QR_RULE_NAME);
            CommonUtils commonUtils = new CommonUtils();
            Map<String, Object> metaMap = getMetaMap();

            JSONArray jsonArrayTokenHolderAddresses = dataObject.optJSONArray(
                    OstConstants.QR_TOKEN_HOLDER_ADDRESSES
            );
            List<String> tokenHolderAddresses = commonUtils.jsonArrayToList(
                    jsonArrayTokenHolderAddresses
            );

            JSONArray jsonArrayAmounts = dataObject.optJSONArray(OstConstants.QR_AMOUNTS);
            List<String> amounts = commonUtils.jsonArrayToList(jsonArrayAmounts);

            OstExecuteTransaction ostExecuteTransaction = new OstExecuteTransaction(userId,
                    tokenHolderAddresses,
                    amounts,
                    ruleName,
                    metaMap,
                    callback);

            ostExecuteTransaction.perform();
        }

        private Map<String, Object> getMetaMap() {
            Map<String, Object> metaMap = new HashMap<>();
            if (null == metaObject) {
                return metaMap;
            }

            String transactionName = metaObject.optString(OstConstants.QR_META_TRANSACTION_NAME,
                    "");
            metaMap.put(OstConstants.META_TRANSACTION_NAME, transactionName);

            String transactionType = metaObject.optString(OstConstants.QR_META_TRANSACTION_TYPE,
                    "");
            metaMap.put(OstConstants.META_TRANSACTION_TYPE, transactionType);

            String transactionDetails = metaObject.optString(OstConstants.QR_META_TRANSACTION_DETAILS,
                    "");
            metaMap.put(OstConstants.META_TRANSACTION_DETAILS, transactionDetails);

            return metaMap;
        }

        @Override
        public void validateApiDependentParams() {

        }
    }
}