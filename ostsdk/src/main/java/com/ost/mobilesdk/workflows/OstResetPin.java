package com.ost.mobilesdk.workflows;

import android.os.Bundle;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstRecoveryOwner;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.security.impls.OstSdkCrypto;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DelayedRecoveryModule;
import com.ost.mobilesdk.utils.EIP712;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstPollingService;
import com.ost.mobilesdk.workflows.services.OstRecoveryPollingService;

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
    private final String mAppSalt;
    private final String mCurrentPin;
    private final String mNewPin;
    private OstResetPin.STATES mCurrentState = OstResetPin.STATES.INITIAL;
    private String mNewRecoveryOwnerAddress;

    public OstResetPin(String userId, String appSalt, String currentPin, String newPin, OstWorkFlowCallback workFlowCallback) {
        super(userId, workFlowCallback);
        mAppSalt = appSalt;
        mCurrentPin = currentPin;
        mNewPin = newPin;
    }

    @Override
    protected AsyncStatus process() {
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

                boolean isValidated = new OstKeyManager(mUserId).validatePin(mCurrentPin, mAppSalt);
                if (!isValidated) {
                    return postErrorInterrupt("wf_rp_pr_3", OstErrors.ErrorCode.INVALID_PIN);
                }

                try {
                    mOstApiClient.getDevices(mOstUser.getCurrentDevice().getAddress());
                } catch (IOException e) {
                    Log.e(TAG, "GetDevice api failed");
                    return postErrorInterrupt("wf_rp_pr_4", OstErrors.ErrorCode.USER_API_FAILED);
                }


                String recoveryOwnerAddress = mOstUser.getRecoveryOwnerAddress();
                String recoveryAddress = mOstUser.getRecoveryAddress();

                Log.i(TAG, "Getting salt");
                String salt = super.getSalt();
                if (null == salt) {
                    Log.e(TAG, "Salt is null");
                    return postErrorInterrupt("wf_rp_pr_3", OstErrors.ErrorCode.SALT_API_FAILED);
                }

                //Todo:: Set recovery hash after successfull revoking;
                mNewRecoveryOwnerAddress = createRecoveryKey(mNewPin, mAppSalt, salt);

                JSONObject jsonObject = new DelayedRecoveryModule().resetRecoveryOwnerData(recoveryOwnerAddress,
                        mNewRecoveryOwnerAddress, recoveryAddress);
                if (null == jsonObject) {
                    return postErrorInterrupt("wf_rp_pr_4", OstErrors.ErrorCode.EIP712_FAILED);
                }

                String eip712Hash = null;
                try {
                    eip712Hash = new EIP712(jsonObject).toEIP712TransactionHash();
                } catch (Exception e) {
                    return postErrorInterrupt("wf_rp_pr_5", OstErrors.ErrorCode.EIP712_FAILED);
                }

                String signature = signData(mCurrentPin, mAppSalt, salt, eip712Hash);

                Map<String, Object> requestMap = buildApiRequest(mNewRecoveryOwnerAddress,
                        recoveryOwnerAddress, recoveryAddress, signature);

                JSONObject postRecoveryAddresssResponse = null;
                try {
                    postRecoveryAddresssResponse = mOstApiClient.postRecoveryOwners(requestMap);
                } catch (IOException e) {
                    Log.e(TAG, "IOException in postRecoveryOwner");
                }

                if (!isValidResponse(postRecoveryAddresssResponse)) {
                    return postErrorInterrupt("wf_rp_pr_5", OstErrors.ErrorCode.POST_RECOVERY_API_FAILED);
                }
            case POLLING:
                OstRecoveryPollingService.startPolling(mUserId, mNewRecoveryOwnerAddress, OstRecoveryOwner.CONST_STATUS.AUTHORIZING,
                        OstRecoveryOwner.CONST_STATUS.AUTHORIZED);

                Log.i(TAG, "Waiting for update");
                Bundle bundle = waitForUpdate(OstSdk.RECOVERY_OWNER, mNewRecoveryOwnerAddress);
                if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
                    Log.d(TAG, String.format("Polling time out for recovery owner Id: %s", mNewRecoveryOwnerAddress));
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

    private Map<String, Object> buildApiRequest(String newRecoveryOwnerAddress, String signer,
                                                String recoveryAddress, String signature) {
        Map<String, Object> map = new HashMap<>();
        map.put(NEW_RECOVERY_OWNER_ADDRESS, newRecoveryOwnerAddress);
        map.put(TO, recoveryAddress);
        map.put(SIGNER, signer);
        map.put(SIGNATURE, signature);
        return map;
    }

    private String signData(String pin, String appSalt, String salt, String eip712Hash) {
        String stringToCalculate = String.format("%s%s%s", appSalt, pin, mUserId);

        byte[] seed = OstSdkCrypto.getInstance().genSCryptKey(stringToCalculate.getBytes(), salt.getBytes());

        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        //Don't store key of recovery key
        String signature = ostKeyManager.signUsingSeed(seed, eip712Hash);

        return signature;
    }

    private String createRecoveryKey(String pin, String appSalt, String salt) {
        String stringToCalculate = String.format("%s%s%s", appSalt, pin, mUserId);

        if (!storePinKeccek(pin, appSalt)) {
            return null;
        }

        byte[] seed = OstSdkCrypto.getInstance().genSCryptKey(stringToCalculate.getBytes(), salt.getBytes());

        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        //Don't store key of recovery key
        String address = ostKeyManager.createHDKeyAddress(seed);

        return address;
    }

    private boolean storePinKeccek(String pin, String appSalt) {
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        return ostKeyManager.storePinHash(pin, appSalt);
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