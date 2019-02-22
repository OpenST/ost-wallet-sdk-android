package com.ost.mobilesdk.workflows;

import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstSession;
import com.ost.mobilesdk.models.entities.OstTransaction;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.EIP1077;
import com.ost.mobilesdk.utils.TokenRules;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstTransactionPollingService;

import org.json.JSONObject;

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
        super(userId ,callback);
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
                status = status.isSuccess() ? super.loadToken() : status;
                if (!status.isSuccess()) return status;

                Log.i(TAG, "Validate states");
                if (!hasActivatedUser()) {
                    return postErrorInterrupt("wf_et_pr_2", OstErrors.ErrorCode.USER_NOT_ACTIVATED);
                }

                if (!hasAuthorizedDevice()) {
                    return postErrorInterrupt("wf_et_pr_3", OstErrors.ErrorCode.DEVICE_UNREGISTERED);
                }

                Log.i(TAG, "Building call data");
                String callData = createCallData();

                Log.i(TAG, "Creating transaction hash to sign");
                String eip1077TxnHash = createEIP1077TxnHash(callData);

                Log.i(TAG, "Signing Transaction using session");
                OstSession session = mOstUser.getActiveSession();
                if (null == session) {
                    return postErrorInterrupt("wf_et_pr_4", OstErrors.ErrorCode.NO_SESSION_FOUND);
                }
                String signer = session.getAddress();
                String signature = signTransaction(session, eip1077TxnHash);

                Log.i(TAG, "Building transaction request");
                Map<String,Object> map = buildTransactionRequest(signature, signer);

                Log.i(TAG, "post transaction execute api");
                String entityId = postTransactionApi(map);
                if (null == entityId) {
                    return postErrorInterrupt("wf_et_pr_5", OstErrors.ErrorCode.TRANSACTION_API_FAILED);
                }

                Log.i(TAG, "start polling");
                OstTransactionPollingService.startPolling(mUserId, entityId,
                        OstTransaction.CONST_STATUS.SUBMITTED, OstTransaction.CONST_STATUS.SUCCESS);

                boolean timeout = waitForUpdate(OstSdk.TRANSACTION, entityId);

                if (timeout) {
                    return postErrorInterrupt("wf_et_pr_6", OstErrors.ErrorCode.POLLING_TIMEOUT);
                }
                return postFlowComplete();
            case CANCELLED:
                Log.d(TAG, String.format("Error in Add device flow: %s", mUserId));
                postErrorInterrupt("wf_pe_pr_7", OstErrors.ErrorCode.WORKFLOW_CANCELED);
                break;
        }
        return new AsyncStatus(true);
    }

    private String postTransactionApi(Map<String, Object> map) {
        return null;
    }

    private Map<String, Object> buildTransactionRequest(String signature, String signer) {
        return null;
    }

    private String signTransaction(OstSession session, String eip1077TxnHash) {
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        return ostKeyManager.signUsingSessionKey(session.getAddress(), eip1077TxnHash);
    }

    private String createEIP1077TxnHash(String callData) {
        JSONObject jsonObject = null;
        String txnHash = null;
        try {
            jsonObject = new EIP1077.TransactionBuilder()
                    .setData(callData)
                    .build();
            txnHash = new EIP1077(jsonObject).toEIP1077TransactionHash();
        } catch (Exception e) {
           Log.e(TAG, "Exception while creating EIP1077 Hash");
           return null;
        }
        return txnHash;
    }

    private String createCallData() {
        return new TokenRules().getAuthorizeSessionExecutableData(mTokenHolderAddresses, mAmounts);
    }
}