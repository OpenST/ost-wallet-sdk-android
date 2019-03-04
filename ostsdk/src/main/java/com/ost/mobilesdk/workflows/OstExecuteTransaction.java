package com.ost.mobilesdk.workflows;

import android.os.Bundle;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstTransaction;
import com.ost.mobilesdk.security.OstTransactionSigner;
import com.ost.mobilesdk.security.structs.SignedTransactionStruct;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstPollingService;
import com.ost.mobilesdk.workflows.services.OstTransactionPollingService;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Execute Transaction
 */
public class OstExecuteTransaction extends OstBaseUserAuthenticatorWorkflow {

    private static final String TAG = "OstExecuteTransaction";
    private static final String DIRECT_TRANSFER = "Direct Transfer";
    private final List<String> mTokenHolderAddresses;
    private final List<String> mAmounts;
    private final String mRuleName;
    private final String mTokenId;

    public OstExecuteTransaction(String userId, String tokenId ,List<String> tokenHolderAddresses, List<String> amounts, String ruleName, OstWorkFlowCallback callback) {
        super(userId, callback);
        mTokenId = tokenId;
        mTokenHolderAddresses = tokenHolderAddresses;
        mAmounts = amounts;
        mRuleName = ruleName;
    }


    @Override
    AsyncStatus performOnAuthenticated() {
        if (!mOstUser.getTokenId().equalsIgnoreCase(mTokenId)) {
            return postErrorInterrupt("wf_et_pr_1", OstErrors.ErrorCode.DIFFERENT_ECONOMY);
        }

        OstTransactionSigner ostTransactionSigner = new OstTransactionSigner(mUserId);
        SignedTransactionStruct signedTransactionStruct = ostTransactionSigner
                .getSignedTransaction(mRuleName, mTokenHolderAddresses, mAmounts);

        Log.i(TAG, "Building transaction request");
        Map<String, Object> map = buildTransactionRequest(signedTransactionStruct);

        Log.i(TAG, "post transaction execute api");
        String entityId = postTransactionApi(map);
        if (null == entityId) {
            return postErrorInterrupt("wf_et_pr_4", OstErrors.ErrorCode.TRANSACTION_API_FAILED);
        }

        Log.i(TAG, "Increment nonce");
        //Increment Nonce
        signedTransactionStruct.getSession().incrementNonce();

        //Request Acknowledge
        postRequestAcknowledge(new OstWorkflowContext(getWorkflowType()),
                new OstContextEntity(OstTransaction.getById(entityId), OstSdk.TRANSACTION));

        Log.i(TAG, "start polling");
        OstTransactionPollingService.startPolling(mUserId, entityId,
                OstTransaction.CONST_STATUS.SUCCESS, OstTransaction.CONST_STATUS.FAILED);

        Bundle bundle = waitForUpdate(OstSdk.TRANSACTION, entityId);
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
            return postFlowComplete();
        }
    }

    @Override
    protected boolean shouldAskForAuthentication() {
        return false;
    }

    @Override
    protected boolean shouldCheckTokenRules() {
        return true;
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
}