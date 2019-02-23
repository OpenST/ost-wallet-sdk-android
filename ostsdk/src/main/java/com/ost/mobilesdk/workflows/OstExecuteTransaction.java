package com.ost.mobilesdk.workflows;

import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstSession;
import com.ost.mobilesdk.models.entities.OstTransaction;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.EIP1077;
import com.ost.mobilesdk.utils.TokenHolder;
import com.ost.mobilesdk.utils.TokenRules;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstTransactionPollingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * To Add Session
 * 1. param validation
 * 2. user activated and device authorized
 * 3. create session keys
 * 4. create payload
 * 5. api post call
 * 6. polling
 */
public class OstExecuteTransaction extends OstBaseWorkFlow {

    private static final String TAG = "OstAddDevice";
    private static final String DIRECT_TRANSFER = "Direct Transfer";
    private final List<String> mTokenHolderAddresses;
    private final List<String> mAmounts;
    private final String mTransactionType;

    private enum STATES {
        INITIAL,
        CANCELLED,
    }

    private STATES mCurrentState = STATES.INITIAL;
    private Object mStateObject = null;

    public OstExecuteTransaction(String userId, List<String> tokenHolderAddresses, List<String> amounts, String transactionType, OstWorkFlowCallback callback) {
        super(userId, callback);
        mTokenHolderAddresses = tokenHolderAddresses;
        mAmounts = amounts;
        mTransactionType = transactionType;
    }

    private void setFlowState(STATES currentState, Object stateObject) {
        this.mCurrentState = currentState;
        this.mStateObject = stateObject;
    }

    synchronized public AsyncStatus process() {
        switch (mCurrentState) {
            case INITIAL:

                Log.d(TAG, String.format("Execute Transaction workflow for userId: %s started", mUserId));

                Log.i(TAG, "Validating user Id");
                if (!hasValidParams()) {
                    return postErrorInterrupt("wf_et_pr_1", OstErrors.ErrorCode.INVALID_WORKFLOW_PARAMS);
                }

                Log.i(TAG, "Loading device and user entities");
                AsyncStatus status = super.loadCurrentDevice();
                status = status.isSuccess() ? super.loadUser() : status;
                status = status.isSuccess() ? super.loadRules() : status;
                if (!status.isSuccess()) return status;

                Log.i(TAG, "Validate states");
                if (!hasActivatedUser()) {
                    return postErrorInterrupt("wf_et_pr_2", OstErrors.ErrorCode.USER_NOT_ACTIVATED);
                }

                if (!hasAuthorizedDevice()) {
                    return postErrorInterrupt("wf_et_pr_3", OstErrors.ErrorCode.DEVICE_UNREGISTERED);
                }

                Log.i(TAG, "Building call data");
                String callData = createCallData(DIRECT_TRANSFER);

                String ruleAddress = getRuleAddressFor(DIRECT_TRANSFER);
                if (null == ruleAddress) {
                    return postErrorInterrupt("wf_et_pr_4", OstErrors.ErrorCode.RULE_NOT_FOUND);
                }

                OstSession session = mOstUser.getActiveSession();
                if (null == session) {
                    return postErrorInterrupt("wf_et_pr_5", OstErrors.ErrorCode.NO_SESSION_FOUND);
                }

                Log.i(TAG, "Creating transaction hash to sign");
                String eip1077TxnHash = createEIP1077TxnHash(callData, ruleAddress, session.getNonce());

                Log.i(TAG, "Signing Transaction using session");
                String signer = session.getAddress();
                String signature = signTransaction(session, eip1077TxnHash);

                Log.i(TAG, "Building transaction request");
                Map<String, Object> map = buildTransactionRequest(ruleAddress, session.getNonce() ,signature, signer, DIRECT_TRANSFER);

                Log.i(TAG, "post transaction execute api");
                String entityId = postTransactionApi(map);
                if (null == entityId) {
                    return postErrorInterrupt("wf_et_pr_6", OstErrors.ErrorCode.TRANSACTION_API_FAILED);
                }

                Log.i(TAG, "start polling");
                OstTransactionPollingService.startPolling(mUserId, entityId,
                        OstTransaction.CONST_STATUS.SUBMITTED, OstTransaction.CONST_STATUS.SUCCESS);

                boolean timeout = waitForUpdate(OstSdk.TRANSACTION, entityId);

                if (timeout) {
                    return postErrorInterrupt("wf_et_pr_7", OstErrors.ErrorCode.POLLING_TIMEOUT);
                }
                return postFlowComplete();
            case CANCELLED:
                Log.d(TAG, String.format("Error in Add device flow: %s", mUserId));
                postErrorInterrupt("wf_pe_pr_8", OstErrors.ErrorCode.WORKFLOW_CANCELED);
                break;
        }
        return new AsyncStatus(true);
    }

    private String getRuleAddressFor(String directTransfer) {
        for (int i =0; i< mOstRules.length; i++) {
            if(directTransfer.equalsIgnoreCase(mOstRules[i].getName())) {
                return mOstRules[i].getAddress();
            }
        }
        return null;
    }

    private String postTransactionApi(Map<String, Object> map) {
        JSONObject jsonObject = null;
        try {
            jsonObject = mOstApiClient.postExecuteTransaction(map);
            OstSdk.parse(jsonObject);
        } catch (IOException e) {
            Log.e(TAG, "IO exception in post Transaction");
            return null;
        } catch (JSONException e) {
            Log.e(TAG, "JSONException in post Transaction");
            return null;
        }
        if (isValidResponse(jsonObject)) {
            return parseResponseForKey(jsonObject, OstTransaction.TRANSACTION_HASH);
        } else {
            return null;
        }
    }

    private Map<String, Object> buildTransactionRequest(String contractAddress, String nonce ,String signature, String signer, String ruleName) {

        String callData = createCallData(ruleName);
        Map<String, Object> rawCallData = createRawCallData(ruleName);
        return new ExecuteRuleRequestBuilder()
                .setToAddress(contractAddress)
                .setCallData(callData)
                .setNonce(nonce)
                .setRawCallData(rawCallData)
                .setSignature(signature)
                .setSigner(signer)
                .build();
    }

    private Map<String, Object> createRawCallData(String ruleName) {
        if (ruleName.equalsIgnoreCase(DIRECT_TRANSFER)) {
            return new TokenRules().getTransactionRawCallData(mTokenHolderAddresses, mAmounts);
        }
        return null;
    }

    private String signTransaction(OstSession session, String eip1077TxnHash) {
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        return ostKeyManager.signUsingSessionKey(session.getAddress(), eip1077TxnHash);
    }

    /**
     * from: tokenHolderAddress,
     * to: ruleContractAddress,
     * value: 0,
     * gasPrice: 0,
     * gas: 0,
     * data: methodEncodedAbi,
     * nonce: keyNonce,
     * callPrefix: callPrefix
     *
     * @param keyNonce
     * @return
     */
    private String createEIP1077TxnHash(String callData, String contractAddress, String keyNonce) {
        JSONObject jsonObject = null;
        String txnHash = null;
        try {
            String tokenHolderAddress = mOstUser.getTokenHolderAddress();
            jsonObject = new EIP1077.TransactionBuilder()
                    .setTo(contractAddress)
                    .setFrom(tokenHolderAddress)
                    .setCallPrefix(new TokenHolder().get_EXECUTABLE_CALL_PREFIX())
                    .setData(callData)
                    .setNonce(keyNonce)
                    .build();
            txnHash = new EIP1077(jsonObject).toEIP1077TransactionHash();
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating EIP1077 Hash");
            return null;
        }
        return txnHash;
    }

    private String createCallData(String ruleName) {
        if (ruleName.equalsIgnoreCase(DIRECT_TRANSFER)) {
            return new TokenRules().getTransactionExecutableData(mTokenHolderAddresses, mAmounts);
        }
        return null;
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

        public ExecuteRuleRequestBuilder setRawCallData(Map<String, Object> rawCallData) {
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

        private Map<String, Object> rawCallData = new HashMap<>();
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
}