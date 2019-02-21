package com.ost.mobilesdk.workflows;

import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.security.impls.OstSdkCrypto;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstUserPollingService;

import org.json.JSONObject;

import java.io.IOException;

public class OstActivateUser extends OstBaseWorkFlow {

    private static final String TAG = "OstActivateUser";
    private final String mPassWord;
    private final String mUPin;
    private String mExpirationHeight;
    private final String mSpendingLimit;
    private final long mExpiresAfterInSecs;

    public OstActivateUser(String userId, String uPin, String password, long expiresAfterInSecs,
                           String spendingLimitInWei, OstWorkFlowCallback callback) {
        super(userId, callback);
        mUPin = uPin;
        mPassWord = password;
        mExpiresAfterInSecs = expiresAfterInSecs;
        mSpendingLimit = spendingLimitInWei;
    }

    @Override
    public OstConstants.WORKFLOW_TYPE getWorkflowType() {
        return OstConstants.WORKFLOW_TYPE.ACTIVATE_USER;
    }

    @Override
    synchronized protected AsyncStatus process() {
        if (!hasValidParams()) {
            Log.i(TAG, "Work flow has invalid params");
            return postErrorInterrupt("wf_au_pr_1", ErrorCode.INVALID_WORKFLOW_PARAMS);
        }

        //Load Current Device.
        AsyncStatus status = super.loadCurrentDevice();

        //Load Current User Information.
        status = status.isSuccess() ? super.loadUser() : status;

        //Check if user is already activating.
        if (hasActivatedUser()) {
            //Exit flow if user already activated.
            Log.i(TAG, "User is already activated");
            postFlowComplete();
            return new AsyncStatus(true);
        }
        //Check if user is already activating.
        else if (hasActivatingUser()) {
            Log.i(TAG, "User is activating... start polling");
        }
        //Activate the user if otherwise.
        else {
            //Load Token Information.
            status = status.isSuccess() ? super.loadToken() : status;

            //Calculate Expiration Height
            status = status.isSuccess() ? this.calculateExpirationHeight() : status;

            if (!status.isSuccess()) {
                return status;
            }

            OstApiClient ostApiClient = this.mOstApiClient;

            Log.i(TAG, "Getting salt");
            String salt = super.getSalt();
            if (null == salt) {
                Log.e(TAG, "Salt is null");
                return postErrorInterrupt("wf_au_pr_2", ErrorCode.SALT_API_FAILED);
            }

            Log.i(TAG, "Creating recovery key");
            String recoveryAddress = createRecoveryKey(salt);

            Log.i(TAG, "Creating session key");
            String sessionAddress = new OstKeyManager(mUserId).createSessionKey();

            Log.i(TAG, "Activate user");
            Log.d(TAG, String.format("Deploying token with SessionAddress: %s, ExpirationHeight: %s," +
                            " SpendingLimit: %s, RecoveryAddress: %s", sessionAddress,
                    mExpirationHeight, mSpendingLimit, recoveryAddress));
            JSONObject response;
            try {
                response = ostApiClient.postUserActivate(sessionAddress,
                        mExpirationHeight, mSpendingLimit, recoveryAddress);
                if (isValidResponse(response)) {
                    //Parse the api response and update the data locally.
                    OstSdk.parse(response);
                } else {
                    //Return with error.
                    Log.e(TAG, String.format("Invalid response for User activate call %s", response.toString()));
                    return postErrorInterrupt("wf_au_pr_3", ErrorCode.ACTIVATE_USER_API_FAILED);
                }
            } catch (Exception e) {
                Log.e(TAG, "Something went wrong while activating user.", e);
                return postErrorInterrupt("wf_au_pr_4", ErrorCode.ACTIVATE_USER_API_FAILED);
            }
        }

        Log.i(TAG, "Starting user polling service");
        OstUserPollingService.startPolling(mUserId, mUserId, OstUser.CONST_STATUS.ACTIVATING,
                OstUser.CONST_STATUS.ACTIVATED);

        Log.i(TAG, "Waiting for update");
        boolean isTimeOut = waitForUpdate(OstSdk.USER, mUserId);
        if (isTimeOut) {
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
        return OstUser.CONST_STATUS.ACTIVATING.equals(OstSdk.getUser(mUserId).getStatus());
    }

    @Override
    boolean hasValidParams() {
        return super.hasValidParams() && !TextUtils.isEmpty(mUPin) && !TextUtils.isEmpty(mPassWord)
                && (mExpiresAfterInSecs > 0) && !TextUtils.isEmpty(mSpendingLimit);
    }

    private boolean hasActivatedUser() {
        return OstUser.CONST_STATUS.ACTIVATED.equals(OstSdk.getUser(mUserId).getStatus());
    }


    private String createRecoveryKey(String salt) {
        String stringToCalculate = String.format("%s%s%s", mPassWord, mUPin, mUserId);
        byte[] seed = OstSdkCrypto.getInstance().genSCryptKey(stringToCalculate.getBytes(), salt.getBytes());

        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        //Don't store key of recovery key
        String address = ostKeyManager.createHDKeyAddress(seed);

        return address;
    }

    private AsyncStatus calculateExpirationHeight() {
        if (null == mOstToken) {
            AsyncStatus loadTokenStatus = super.loadToken();
            if (!loadTokenStatus.isSuccess()) {
                return loadTokenStatus;
            }
        }

        JSONObject jsonObject = null;
        long currentBlockNumber, blockGenerationTime;
        try {
            jsonObject = mOstApiClient.getCurrentBlockNumber();
            String strCurrentBlockNumber = parseResponseForKey(jsonObject, OstConstants.BLOCK_HEIGHT);
            String strBlockGenerationTime = parseResponseForKey(jsonObject, OstConstants.BLOCK_TIME);
            currentBlockNumber = Long.parseLong(strCurrentBlockNumber);
            blockGenerationTime = Long.parseLong(strBlockGenerationTime);

        } catch (IOException e) {

            Log.e(TAG, "Encountered IOException while fetching current block number.", e);
            return postErrorInterrupt("wp_au_ceh_1", ErrorCode.CHAIN_API_FAILED);
        } catch (Exception e) {
            Log.e(TAG, "Encountered Exception while fetching current block number.", e);
            return postErrorInterrupt("wp_au_ceh_2", ErrorCode.CHAIN_API_FAILED);
        }

        long bufferBlocks = OstConstants.SESSION_BUFFER_TIME / blockGenerationTime;
        long expiresAfterBlocks = mExpiresAfterInSecs / blockGenerationTime;
        long expirationHeight = currentBlockNumber + expiresAfterBlocks + bufferBlocks;

        mExpirationHeight = String.valueOf(expirationHeight);
        return new AsyncStatus(true);
    }
}