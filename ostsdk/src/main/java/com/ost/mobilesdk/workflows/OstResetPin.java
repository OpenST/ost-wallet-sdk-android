package com.ost.mobilesdk.workflows;

import android.os.Bundle;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstRecoveryOwner;
import com.ost.mobilesdk.security.OstRecoveryManager;
import com.ost.mobilesdk.security.UserPassphrase;
import com.ost.mobilesdk.security.structs.SignedResetRecoveryStruct;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstPollingService;
import com.ost.mobilesdk.workflows.services.OstRecoveryPollingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OstResetPin extends OstBaseWorkFlow {

    private static final String TAG = "OstResetPin";
    private static final String NEW_RECOVERY_OWNER_ADDRESS = "new_recovery_owner_address";
    private static final String TO = "to";
    private static final String SIGNER = "signer";
    private static final String SIGNATURE = "signature";
    private final UserPassphrase currentPassphrase;
    private final UserPassphrase newPassphrase;

    private String mNewRecoveryOwnerAddress;
    private OstResetPin.STATES mCurrentState = OstResetPin.STATES.INITIAL;


    public OstResetPin(String userId, UserPassphrase currentPassphrase, UserPassphrase newPassphrase, OstWorkFlowCallback workFlowCallback) {
        super(userId, workFlowCallback);
        this.currentPassphrase = currentPassphrase;
        this.newPassphrase = newPassphrase;
    }

    @Override
    protected AsyncStatus process() {
        String newRecoveryOwnerAddress = "";
        switch (mCurrentState) {
            case INITIAL:
                Log.d(TAG, String.format("Perform workflow for userId: %s started", mUserId));

                Log.i(TAG, "validate params");
                if (!hasValidParams()) {
                    return postErrorInterrupt("wf_rp_pr_1", OstErrors.ErrorCode.INVALID_WORKFLOW_PARAMS);
                }

                Log.i(TAG, "Loading device and user entities");
                AsyncStatus status = super.loadCurrentDevice();
                status = status.isSuccess() ? super.loadUser() : status;

                if (!status.isSuccess()) {
                    Log.e(TAG, String.format("Fetching of basic entities failed for user id: %s", mUserId));
                    return status;
                }

                if (!hasActivatedUser()) {
                    Log.e(TAG, String.format("User is not activated of user id: %s", mUserId));
                    return postErrorInterrupt("wf_rp_pr_2", OstErrors.ErrorCode.USER_NOT_ACTIVATED);
                }


                try {
                    mOstApiClient.getDevice(mOstUser.getCurrentDevice().getAddress());
                } catch (IOException e) {
                    Log.e(TAG, "GetDevice api failed");
                    return postErrorInterrupt("wf_rp_pr_4", OstErrors.ErrorCode.GET_USER_API_FAILED);
                }


                SignedResetRecoveryStruct struct;
                OstRecoveryManager rkm;
                try {
                    rkm = new OstRecoveryManager(mUserId);
                    struct = rkm.getResetRecoveryOwnerSignature(currentPassphrase,newPassphrase);
                    rkm = null;
                } catch (OstError error) {
                    return postErrorInterrupt(error.getInternalErrorCode(), error.getErrorCode() );
                }

                newRecoveryOwnerAddress = struct.getNewRecoveryOwnerAddress();
                mNewRecoveryOwnerAddress = newRecoveryOwnerAddress;
                Map<String, Object> requestMap = buildApiRequest(newRecoveryOwnerAddress,
                        struct.getRecoveryOwnerAddress(), struct.getRecoveryContractAddress(), struct.getSignature());

                JSONObject postRecoveryAddresssResponse = null;
                try {
                    postRecoveryAddresssResponse = mOstApiClient.postRecoveryOwners(requestMap);
                } catch (IOException e) {
                    Log.e(TAG, "IOException in postRecoveryOwner");
                }

                if (!isValidResponse(postRecoveryAddresssResponse)) {
                    return postErrorInterrupt("wf_rp_pr_5", OstErrors.ErrorCode.POST_RESET_RECOVERY_API_FAILED);
                }

                JSONObject jsonData = postRecoveryAddresssResponse.optJSONObject(OstConstants.RESPONSE_DATA);
                JSONObject resultTypeObject = jsonData.optJSONObject(jsonData.optString(OstConstants.RESULT_TYPE));
                OstRecoveryOwner ostRecoveryOwner = null;
                try {
                    ostRecoveryOwner = OstRecoveryOwner.parse(resultTypeObject);
                } catch (JSONException e) {
                    return postErrorInterrupt("wf_rp_pr_5", OstErrors.ErrorCode.POST_RESET_RECOVERY_API_FAILED);
                }

                postRequestAcknowledge(new OstWorkflowContext(getWorkflowType()), new OstContextEntity(ostRecoveryOwner, OstSdk.RECOVERY_OWNER));

            case POLLING:

                OstRecoveryPollingService.startPolling(mUserId, mNewRecoveryOwnerAddress, OstRecoveryOwner.CONST_STATUS.AUTHORIZED,
                        OstRecoveryOwner.CONST_STATUS.AUTHORIZATION_FAILED);
                
                Log.i(TAG, "Waiting for update");
                Bundle bundle = waitForUpdate(OstSdk.RECOVERY_OWNER, newRecoveryOwnerAddress);
                if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
                    Log.d(TAG, String.format("Polling time out for recovery owner Id: %s", newRecoveryOwnerAddress));
                    return postErrorInterrupt("wf_adwq_pr_5", OstErrors.ErrorCode.POLLING_TIMEOUT);
                }

                Log.i(TAG, "Response received for RecoveryOwner");
                postFlowComplete();
                break;
            case CANCELLED:
                Log.d(TAG, String.format("Error in Add device flow: %s", mUserId));
                postErrorInterrupt("wf_pe_pr_3", OstErrors.ErrorCode.WORKFLOW_CANCELED);
                break;
        }
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
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.PIN_RESET;
    }

    private enum STATES {
        INITIAL,
        CANCELLED,
        POLLING
    }
}