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

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.OstTransactionSigner;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedTransactionStruct;
import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.models.entities.OstRule;
import com.ost.walletsdk.models.entities.OstTransaction;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.network.OstApiError;
import com.ost.walletsdk.network.polling.OstTransactionPollingHelper;
import com.ost.walletsdk.network.polling.interfaces.OstTransactionPollingCallback;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.utils.CommonUtils;
import com.ost.walletsdk.workflows.OstWorkflowContext.WORKFLOW_TYPE;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstTransactionWorkflowCallback;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class OstExecuteTransaction extends OstBaseWorkFlow implements OstTransactionPollingCallback {

    private static final String TAG = "OstExecuteTransaction";
    private final List<String> mTokenHolderAddresses;
    private final List<String> mAmounts;
    private final String mRuleName;
    private String transactionId;
    private String sessionAddress;
    private final Map<String, Object> mMeta;
    private Map<String, Object> mOptions;

    public OstExecuteTransaction(String userId,
                                 List<String> tokenHolderAddresses,
                                 List<String> amounts,
                                 String ruleName,
                                 Map<String, Object> meta,
                                 Map<String, Object> options,
                                 OstWorkFlowCallback callback) {
        super(userId,
                null == options.get(OstSdk.WAIT_FOR_FINALIZATION) ? true: (Boolean) options.get(OstSdk.WAIT_FOR_FINALIZATION),
                callback);

        mTokenHolderAddresses = tokenHolderAddresses;
        mAmounts = amounts;
        mRuleName = ruleName;
        mOptions = options;
        mMeta = meta;
    }


    @Override
    AsyncStatus performOnAuthenticated() {

        OstTransactionSigner ostTransactionSigner = new OstTransactionSigner(mUserId);
        SignedTransactionStruct signedTransactionStruct = ostTransactionSigner
                .getSignedTransaction(mRuleName, mOptions, mTokenHolderAddresses, mAmounts, getRuleAddressFor(mRuleName));

        Log.i(TAG, "Building transaction request");
        Map<String, Object> map = buildTransactionRequest(signedTransactionStruct);

        Log.i(TAG, "post transaction execute api");
        try {
            transactionId = postTransactionApi(map);
        } catch (OstApiError ostApiError) {
            handleSessionSync(signedTransactionStruct.getSignerAddress());
        }

        Log.i(TAG, "Increment nonce");
        //Increment Nonce
        signedTransactionStruct.getSession().incrementNonce();

        //Store Session address
        sessionAddress = signedTransactionStruct.getSignerAddress();

        //Request Acknowledge
        postRequestAcknowledge(getWorkflowContext(),
                new OstContextEntity(OstTransaction.getById(transactionId), OstSdk.TRANSACTION));

        Log.i(TAG, "start polling");
        //OstTransactionPollingService
        if (mShouldPoll) {
            new OstTransactionPollingHelper(transactionId, mUserId, this);
        } else {
            postFlowComplete(new OstContextEntity(OstTransaction.getById(transactionId), OstSdk.TRANSACTION));
            goToState(WorkflowStateManager.COMPLETED);
        }
        return new AsyncStatus(true);
    }

    @Override
    public void onTransactionMined(@NonNull OstTransaction transaction) {
        Log.e(TAG, "Transaction " + this.transactionId + " mined.");
        if ( getCallback() instanceof OstTransactionWorkflowCallback) {
            OstContextEntity tx = new OstContextEntity(OstTransaction.getById(transactionId), OstSdk.TRANSACTION);
            OstWorkflowContext context = getWorkflowContext();
            OstTransactionWorkflowCallback cb = (OstTransactionWorkflowCallback) getCallback();
            // Callback on main thread.
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    cb.transactionMined(context, tx);
                }
            });
        }
    }

    @Override
    public void onOstPollingSuccess(@Nullable OstBaseEntity entity, @Nullable JSONObject data) {
        Log.e(TAG, "Transaction " + this.transactionId + " executed successfully.");
        postFlowComplete(new OstContextEntity(OstTransaction.getById(this.transactionId), OstSdk.TRANSACTION));
        goToState(WorkflowStateManager.COMPLETED);
    }

    @Override
    public void onOstPollingFailed(OstError error) {
        Log.e(TAG, "Transaction " + this.transactionId + " failed");
        Log.d(TAG, "Syncing session");
        try {
            mOstApiClient.getSession(sessionAddress);
        } catch (Throwable e) {
            //Ignore.
            Log.d(TAG, "Failed to sync session entity");
        }
        postErrorInterrupt( error );
        goToState(WorkflowStateManager.COMPLETED_WITH_ERROR);
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
        JSONObject jsonObject = mOstApiClient.postExecuteTransaction(map);
        if (isValidResponse(jsonObject)) {
            String txId = parseResponseForKey(jsonObject, OstTransaction.ID);
            if ( null != txId ) {
                return txId;
            }
        }
        throw OstError.ApiResponseError("wf_et_ptxapi_1", "postTransactionApi", jsonObject);
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

    private void handleSessionSync(String sessionAddress) {
        try {
            mOstApiClient.getSession(sessionAddress);
        } catch (OstApiError ostApiError) {
            if (ostApiError.isNotFound()) {
                new OstSdkSync(mUserId, OstSdkSync.SYNC_ENTITY.SESSION).perform();
            }
        }
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
                jsonObject.put(OstConstants.TRANSACTION_OPTIONS,
                        dataObject.optJSONObject(OstConstants.QR_OPTIONS_DATA));
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

            Map<String, Object> options = new HashMap<>();
            JSONObject ruleNameJSONObject = dataObject.optJSONObject(OstConstants.QR_OPTIONS_DATA);
            if (null != ruleNameJSONObject) {
                String currencyCode = ruleNameJSONObject.optString(OstConstants.QR_CURRENCY_CODE);
                if (!TextUtils.isEmpty(currencyCode)) {
                    options.put(OstSdk.CURRENCY_CODE, currencyCode);
                }
            }

            OstExecuteTransaction ostExecuteTransaction = new OstExecuteTransaction(userId,
                    tokenHolderAddresses,
                    amounts,
                    ruleName,
                    metaMap,
                    options,
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
            if (!TextUtils.isEmpty(transactionName)) {
                metaMap.put(OstConstants.META_TRANSACTION_NAME, transactionName);
            }

            String transactionType = metaObject.optString(OstConstants.QR_META_TRANSACTION_TYPE,
                    "");
            if (!TextUtils.isEmpty(transactionType)) {
                metaMap.put(OstConstants.META_TRANSACTION_TYPE, transactionType);
            }

            String transactionDetails = metaObject.optString(OstConstants.QR_META_TRANSACTION_DETAILS,
                    "");
            if (!TextUtils.isEmpty(transactionDetails)) {
                metaMap.put(OstConstants.META_TRANSACTION_DETAILS, transactionDetails);
            }

            return metaMap;
        }

        @Override
        public void validateApiDependentParams() {

        }

        @Override
        public WORKFLOW_TYPE getWorkFlowType() {
            return WORKFLOW_TYPE.EXECUTE_TRANSACTION;
        }
    }

    public static Map<String, Object> convertMetaMap(JSONObject metaJsonObject) {
        Map<String, Object> metaMap = new HashMap<>();
        if (null == metaJsonObject) {
            return metaMap;
        }

        String transactionName = metaJsonObject.optString(OstConstants.META_TRANSACTION_NAME,
                "");
        if (!TextUtils.isEmpty(transactionName)) {
            metaMap.put(OstConstants.META_TRANSACTION_NAME, transactionName);
        }

        String transactionType = metaJsonObject.optString(OstConstants.META_TRANSACTION_TYPE,
                "");
        if (!TextUtils.isEmpty(transactionType)) {
            metaMap.put(OstConstants.META_TRANSACTION_TYPE, transactionType);
        }

        String transactionDetails = metaJsonObject.optString(OstConstants.META_TRANSACTION_DETAILS,
                "");
        if (!TextUtils.isEmpty(transactionDetails)) {
            metaMap.put(OstConstants.META_TRANSACTION_DETAILS, transactionDetails);
        }

        return metaMap;
    }
}