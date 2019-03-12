package com.ost.mobilesdk.workflows;

import android.os.Bundle;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.ecKeyInteracts.OstKeyManager;
import com.ost.mobilesdk.ecKeyInteracts.OstRecoveryManager;
import com.ost.mobilesdk.ecKeyInteracts.UserPassphrase;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstPollingService;
import com.ost.mobilesdk.workflows.services.OstUserPollingService;

import org.json.JSONObject;

import java.io.IOException;

public class OstActivateUser extends OstBaseWorkFlow {

    private static final String TAG = "OstActivateUser";
    private final UserPassphrase mPassphrase;
    private String expirationHeight;
    private final String mSpendingLimit;
    private final long mExpiresAfterInSecs;

    public OstActivateUser(UserPassphrase passphrase, long expiresAfterInSecs,
                           String spendingLimitInWei, OstWorkFlowCallback callback) {
        super(passphrase.getUserId(), callback);
        mPassphrase = passphrase;
        mExpiresAfterInSecs = expiresAfterInSecs;
        mSpendingLimit = spendingLimitInWei;
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER;
    }

    @Override
    synchronized protected AsyncStatus process() {
        if (!hasValidParams()) {
            Log.i(TAG, "Work flow has invalid params");
            return postErrorInterrupt("wf_au_pr_1", ErrorCode.INVALID_WORKFLOW_PARAMS);
        }

        //Load Current Device.
        try {
            //Perform Validations.
            ensureApiCommunication();
            ensureOstUser();
            if ( mOstUser.isActivated() ) {
                throw new OstError("wf_au_pr_2", ErrorCode.USER_ALREADY_ACTIVATED);
            } else if ( mOstUser.isActivating() ) {
                throw new OstError("wf_au_pr_3", ErrorCode.USER_ACTIVATING);
            }

            ensureOstToken();

            String expirationHeight = this.calculateExpirationHeight();



            // Compute recovery address.
            String recoveryAddress = new OstRecoveryManager(mUserId).getRecoveryAddressFor(mPassphrase);

            // Create session key
            String sessionAddress = new OstKeyManager(mUserId).createSessionKey();

            // Post the Api call.
            Log.i(TAG, "Activate user");
            Log.d(TAG, String.format("SessionAddress: %s, expirationHeight: %s,"
                            + " SpendingLimit: %s, RecoveryAddress: %s", sessionAddress,
                    expirationHeight, mSpendingLimit, recoveryAddress));

            OstApiClient ostApiClient = this.mOstApiClient;

            JSONObject response = ostApiClient.postUserActivate(sessionAddress,
                    expirationHeight, mSpendingLimit, recoveryAddress);

            if ( !isValidResponse(response)) {
                throw new OstError("wf_au_pr_4", ErrorCode.ACTIVATE_USER_API_FAILED);
            }

            // Let the app know that kit has accepted the request.
            OstWorkflowContext workflowContext = new OstWorkflowContext(getWorkflowType());
            OstContextEntity ostContextEntity = new OstContextEntity(OstUser.getById(mUserId), OstSdk.USER);
            postRequestAcknowledge(workflowContext, ostContextEntity);

        } catch (OstError error) {
            return postErrorInterrupt(error);
        } catch (IOException e) {
            OstError error = new OstError("wf_au_pr_4", ErrorCode.ACTIVATE_USER_API_FAILED);
            return postErrorInterrupt(error);
        } finally {
            mPassphrase.wipe();
        }

        //Activate the user if otherwise.
        Log.i(TAG, "Starting user polling service");
        Log.i(TAG, "Waiting for update");
        Bundle bundle = OstUserPollingService.startPolling(mUserId, mUserId, OstUser.CONST_STATUS.ACTIVATED,
                OstUser.CONST_STATUS.CREATED);
        if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
            Log.d(TAG, String.format("Polling time out for user Id: %s", mUserId));
            return postErrorInterrupt("wf_au_pr_5", ErrorCode.ACTIVATE_USER_API_POLLING_FAILED);
        }
        Log.i(TAG, "Syncing Entities: User, Device, Sessions");
        new OstSdkSync(mUserId, OstSdkSync.SYNC_ENTITY.USER, OstSdkSync.SYNC_ENTITY.DEVICE,
                OstSdkSync.SYNC_ENTITY.SESSION).perform();

        Log.i(TAG, "Response received for post Token deployment");
        postFlowComplete();

        return new AsyncStatus(true);
    }

    private boolean hasActivatingUser() {
        return OstSdk.getUser(mUserId).isActivating();
    }


    private String calculateExpirationHeight() {
        JSONObject jsonObject = null;
        long currentBlockNumber, blockGenerationTime;
        String strCurrentBlockNumber;
        String strBlockGenerationTime;
        try {
            jsonObject = mOstApiClient.getCurrentBlockNumber();
            strCurrentBlockNumber = parseResponseForKey(jsonObject, OstConstants.BLOCK_HEIGHT);
            strBlockGenerationTime = parseResponseForKey(jsonObject, OstConstants.BLOCK_TIME);
        } catch (Throwable e) {
            throw new OstError("wf_au_ceh_1", ErrorCode.CHAIN_API_FAILED);
        }

        currentBlockNumber = Long.parseLong(strCurrentBlockNumber);
        blockGenerationTime = Long.parseLong(strBlockGenerationTime);
        long bufferBlocks = OstConstants.OST_SESSION_BUFFER_TIME / blockGenerationTime;
        long expiresAfterBlocks = mExpiresAfterInSecs / blockGenerationTime;
        long expirationHeight = currentBlockNumber + expiresAfterBlocks + bufferBlocks;

        return String.valueOf(expirationHeight);
    }
}