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

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.ost.walletsdk.OstConfigs;
import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.biometric.OstBiometricAuthentication;
import com.ost.walletsdk.ecKeyInteracts.OstKeyManager;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedAddDeviceStruct;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstDeviceManager;
import com.ost.walletsdk.models.entities.OstDeviceManagerOperation;
import com.ost.walletsdk.models.entities.OstRule;
import com.ost.walletsdk.models.entities.OstToken;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.network.OstApiClient;
import com.ost.walletsdk.network.OstApiError;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.utils.CommonUtils;
import com.ost.walletsdk.utils.EIP712;
import com.ost.walletsdk.utils.GnosisSafe;
import com.ost.walletsdk.utils.OstPayloadBuilder;
import com.ost.walletsdk.workflows.OstWorkflowContext.WORKFLOW_TYPE;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

abstract class OstBaseWorkFlow {
    private static final String TAG = "OstBaseWorkFlow";
    private final static ThreadPoolExecutor THREAD_POOL_EXECUTOR = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(1);

    final String mUserId;
    final Handler mHandler;

    private final WeakReference <OstWorkFlowCallback> workFlowCallbackWeakReference;

    protected OstWorkFlowCallback getCallback() {
        return workFlowCallbackWeakReference.get();
    }
    final OstApiClient mOstApiClient;
    private OstBiometricAuthentication.Callback mBioMetricCallBack;

    /**
     * @param userId
     * @param handler
     * @param callback
     * @Depricated
     */
    @Deprecated
    OstBaseWorkFlow(String userId, Handler handler, OstWorkFlowCallback callback) {
        this(userId, callback);
    }

    OstBaseWorkFlow(String userId, OstWorkFlowCallback callback) {
        mUserId = userId;

        mHandler = new Handler(Looper.getMainLooper());
        workFlowCallbackWeakReference = new WeakReference<>(callback);
        mOstApiClient = new OstApiClient(mUserId);
    }

    boolean hasValidParams() {
        return !TextUtils.isEmpty(mUserId) && null != mHandler && null != getCallback();
    }

    /**
     * Method that can be called to validate and params.
     * @Dev: Please make sure this method is only used to perform validations
     * that do not need API calls. For any validation that needs API call, please
     * use onUserDeviceValidationPerformed.
     */
    void ensureValidParams() {
        if ( TextUtils.isEmpty(mUserId) ) {
            throw new OstError("wf_bwf_evp_1", ErrorCode.INVALID_USER_ID);
        }

        if ( null == getCallback() ) {
            throw new OstError("wf_bwf_evp_2", ErrorCode.INVALID_WORKFLOW_CALLBACK);
        }

    }

    public Future<AsyncStatus> perform() {
        return getAsyncQueue().submit(new Callable<AsyncStatus>() {
            @Override
            public AsyncStatus call() {
                return process();
            }
        });
    }

    ThreadPoolExecutor getAsyncQueue() {
        return THREAD_POOL_EXECUTOR;
    }

    protected abstract AsyncStatus process();

    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.UNKNOWN;
    }

    AsyncStatus postFlowComplete(OstContextEntity ostContextEntity) {
        Log.i(TAG, "Flow complete");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OstWorkFlowCallback callback = getCallback();
                if ( null != callback ) {
                    callback.flowComplete(new OstWorkflowContext(getWorkflowType()), ostContextEntity);
                }
            }
        });
        return new AsyncStatus(true);
    }

    AsyncStatus postFlowComplete() {
        return postFlowComplete(null);
    }

    AsyncStatus postErrorInterrupt(String internalErrCode, OstErrors.ErrorCode errorCode) {
        Log.i(TAG, "Flow Error");
        OstError error = new OstError(internalErrCode, errorCode);
        return postErrorInterrupt(error);
    }
    AsyncStatus postErrorInterrupt(OstError error) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OstWorkFlowCallback callback = getCallback();
                if ( null != callback ) {
                    callback.flowInterrupt(new OstWorkflowContext(getWorkflowType()), error);
                }
            }
        });
        return new AsyncStatus(false);
    }

    void postRequestAcknowledge(OstContextEntity ostContextEntity) {
        OstWorkflowContext workflowContext = new OstWorkflowContext(getWorkflowType());
        postRequestAcknowledge(workflowContext, ostContextEntity);
    }

    void postRequestAcknowledge(OstWorkflowContext workflowContext, OstContextEntity ostContextEntity) {
        Log.i(TAG, "Request Acknowledge");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OstWorkFlowCallback callback = getCallback();
                if ( null != callback ) {
                    callback.requestAcknowledged(workflowContext, ostContextEntity);
                }
            }
        });
    }

    void postVerifyData(WORKFLOW_TYPE workFlowType,
                        OstContextEntity ostContextEntity,
                        OstVerifyDataInterface ostVerifyDataInterface) {

        Log.i(TAG, "Post Verify data");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OstWorkFlowCallback callback = getCallback();
                if ( null != callback ) {
                    callback.verifyData(
                            new OstWorkflowContext(workFlowType),
                            ostContextEntity,
                            ostVerifyDataInterface
                    );
                }
            }
        });
    }

    OstBiometricAuthentication.Callback getBioMetricCallBack() {
        if (null == mBioMetricCallBack) {
            mBioMetricCallBack = new OstBiometricAuthentication.Callback() {
                @Override
                public void onAuthenticated() {
                    Log.d(TAG, "Biometric authentication success");
                    onBioMetricAuthenticationSuccess();
                }

                @Override
                public void onError() {
                    Log.d(TAG, "Biometric authentication fail");
                    onBioMetricAuthenticationFail();
                }
            };
        }
        return mBioMetricCallBack;
    }

    void onBioMetricAuthenticationFail() {
    }
    void onBioMetricAuthenticationSuccess() {
    }

    boolean hasDeviceApiKey(OstDevice ostDevice) {
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        return ostKeyManager.getApiKeyAddress().equalsIgnoreCase(ostDevice.getApiSignerAddress());
    }

    boolean canDeviceMakeApiCall(OstDevice ostDevice) {
        //Must have Device Api Key which should have been registered.
        return hasDeviceApiKey(ostDevice) && ostDevice.canMakeApiCall();
    }

    String parseResponseForKey(JSONObject jsonObject, String key) {
        String value = null;
        try {
            if (!jsonObject.getBoolean(OstConstants.RESPONSE_SUCCESS)) {
                Log.e(TAG, "JSON response false");
                return null;
            }
            JSONObject jsonData = jsonObject.getJSONObject(OstConstants.RESPONSE_DATA);

            JSONObject resultTypeObject = jsonData.getJSONObject(jsonData.getString(OstConstants.RESULT_TYPE));

            value = resultTypeObject.getString(key);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception");
        }
        return value;
    }

    boolean isValidResponse(JSONObject jsonObject) {
        try {
            if (jsonObject.getBoolean(OstConstants.RESPONSE_SUCCESS)) {
                return true;
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception");
        }
        return false;
    }




    //region - Ensure Data
    OstDevice mCurrentDevice;
    boolean hasSyncedDeviceToEnsureApiCommunication = false;
    void ensureApiCommunication() throws OstError {
        OstUser ostUser = OstUser.getById(mUserId);

        if ( null == ostUser ) {
            throw new OstError("wp_base_apic_1", ErrorCode.DEVICE_NOT_SETUP);
        }

        OstDevice ostDevice = ostUser.getCurrentDevice();
        if ( null == ostDevice) {
            throw new OstError("wp_base_apic_2", ErrorCode.DEVICE_NOT_SETUP);
        }
        else if ( !canDeviceMakeApiCall(ostDevice) ) {
            String deviceAddress = ostDevice.getAddress();
            // Lets try and make an api call.
            hasSyncedDeviceToEnsureApiCommunication = true;
            try {
                syncCurrentDevice();
            } catch (OstError ostError) {

                if ( ostError.isApiError() ) {
                    OstApiError apiError = (OstApiError) ostError;
                    if ( apiError.isApiSignerUnauthorized() ) {
                        //We know this could happen. Lets ignore the error given by syncCurrentDevice.
                        throw new OstError("wp_base_apic_3", ErrorCode.DEVICE_NOT_SETUP);
                    }
                }
                throw ostError;
            }

            ostDevice = OstDevice.getById(deviceAddress);

            //Check again.
            if ( !canDeviceMakeApiCall(ostDevice) ) {
                throw new OstError("wp_base_apic_4", ErrorCode.DEVICE_NOT_SETUP);
            }
        }
        mCurrentDevice = ostDevice;
    }

    OstUser mOstUser;
    void ensureOstUser() throws OstError {
        ensureOstUser(false);
    }
    void ensureOstUser(boolean forceSync) throws OstError {
        mOstUser = OstUser.getById(mUserId);
        if ( forceSync || null == mOstUser || TextUtils.isEmpty(mOstUser.getTokenHolderAddress()) || TextUtils.isEmpty(mOstUser.getDeviceManagerAddress())) {
            try {
                mOstApiClient.getUser();
                mOstUser = OstUser.getById(mUserId);
            } catch (IOException e) {
                Log.d(TAG, "Encountered IOException while fetching user.");
                OstError ostError = new OstError("wp_base_eou_1", ErrorCode.GET_USER_API_FAILED);
                throw ostError;
            }
        }
    }

    OstToken mOstToken;
    void ensureOstToken()  throws OstError {
        if (null == mOstUser) {
            ensureOstUser();
        }
        String tokenId = mOstUser.getTokenId();
        mOstToken = OstToken.getById(tokenId);
        if (null == mOstToken || TextUtils.isEmpty(mOstToken.getChainId()) ||
                TextUtils.isEmpty(mOstToken.getBtDecimals())) {
            //Make API Call.
            try {
                mOstApiClient.getToken();
                mOstToken = OstToken.getById(tokenId);
            } catch (IOException e) {
                Log.i(TAG, "Encountered IOException while fetching token.");
                throw new OstError("wp_base_eot_1", ErrorCode.TOKEN_API_FAILED);
            }
        }
    }

    void ensureDeviceAuthorized() throws OstError {

        if ( null == mCurrentDevice ) {  ensureApiCommunication(); }

        if ( !mCurrentDevice.isAuthorized() && !hasSyncedDeviceToEnsureApiCommunication ) {
            //Lets sync Device Information.
            syncCurrentDevice();

            //Check Again
            if ( !mCurrentDevice.isAuthorized() ) {
                throw new OstError("wp_base_eda_1", ErrorCode.DEVICE_UNAUTHORIZED);
            }
        }
    }

    void syncCurrentDevice() throws OstError {
        OstUser ostUser = OstUser.getById(mUserId);
        if ( null == ostUser ) {
            throw new OstError("wp_base_scd_1", ErrorCode.DEVICE_NOT_SETUP);
        }
        OstDevice device = ostUser.getCurrentDevice();
        if ( null == device ) {
            throw new OstError("wp_base_scd_1", ErrorCode.DEVICE_NOT_SETUP);
        }
        String currentDeviceAddress = device.getAddress();
        try {
            mOstApiClient.getDevice( currentDeviceAddress );
        } catch (IOException e) {
            throw new OstError("wp_base_scd_3", ErrorCode.GET_DEVICE_API_FAILED);
        }
    }

    OstDeviceManager mDeviceManager;
    void ensureDeviceManager() throws OstError {
        if ( null == mOstUser ) {  ensureOstUser(); }
        String deviceManagerAddress = mOstUser.getDeviceManagerAddress();

        if ( null == deviceManagerAddress ) {
            throw new OstError("wp_base_edm_1", ErrorCode.USER_NOT_ACTIVATED);
        }

        mDeviceManager = OstDeviceManager.getById( deviceManagerAddress );
        if ( null == mDeviceManager ) {
            mDeviceManager = syncDeviceManager();
        }
    }

    OstDeviceManager syncDeviceManager()  throws OstError {
        if ( null == mOstUser ) {  ensureOstUser(); }
        String deviceManagerAddress = mOstUser.getDeviceManagerAddress();
        if ( null == deviceManagerAddress ) {
            throw new OstError("wp_base_sdm_1", ErrorCode.USER_NOT_ACTIVATED);
        }
        try {
            mOstApiClient.getDeviceManager();
            mDeviceManager = OstDeviceManager.getById( deviceManagerAddress );
            return mDeviceManager;
        } catch (IOException e) {
            throw new OstError("wp_base_sdm_2", ErrorCode.DEVICE_MANAGER_API_FAILED);
        }
    }

    OstRule[] mOstRules;
    OstRule[] ensureOstRules(String ruleName)  throws OstError {
        if ( null == mOstToken ) {  ensureOstToken(); }

        mOstRules = mOstToken.getAllRules();
        if ( null != mOstRules && mOstRules.length > 0 ) {
            int len = mOstRules.length;
            for(int cnt = 0; cnt < len; cnt++ ) {
                OstRule rule = mOstRules[cnt];
                if ( rule.getName().equalsIgnoreCase(ruleName) ) {
                    return mOstRules;
                }
            }
        }

        //Fetch the rules.
        try {
            JSONObject rulesResponseObject = mOstApiClient.getAllRules();
            JSONArray rulesJsonArray = (JSONArray)new CommonUtils().parseResponseForResultType(rulesResponseObject);

            int numberOfRules = rulesJsonArray.length();
            OstRule[] ostRules = new OstRule[numberOfRules];
            for (int i=0; i<numberOfRules; i++) {
                ostRules[i] = OstRule.parse(
                        rulesJsonArray.getJSONObject(i)
                );
            }
            mOstRules = ostRules;
        } catch (Exception e) {
            OstError ostError = new OstError("wp_base_eot_1", ErrorCode.RULES_API_FAILED);
            throw ostError;
        }
        return mOstRules;
    }


    @Deprecated
    AsyncStatus loadCurrentDevice() {
        try {
            ensureApiCommunication();
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        }
        return new AsyncStatus(true);
    }

    @Deprecated
    AsyncStatus loadUser() {
        try {
            ensureOstUser();
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        }
        return new AsyncStatus(true);
    }

    @Deprecated
    AsyncStatus loadToken() {
        try {
            ensureOstToken();
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        }
        return new AsyncStatus(true);
    }

    boolean shouldAskForBioMetric() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) OstSdk.getContext()
                    .getSystemService(Context.FINGERPRINT_SERVICE);
            return null != fingerprintManager && fingerprintManager.isHardwareDetected()
                    && fingerprintManager.hasEnrolledFingerprints();
        }
        return false;
    }

    boolean hasActivatedUser() {
        OstUser ostUser = OstUser.getById(mUserId);
        return ostUser.getStatus().equalsIgnoreCase(OstUser.CONST_STATUS.ACTIVATED);
    }

    boolean hasAuthorizedDevice() {
        OstDevice ostDevice = OstUser.getById(mUserId).getCurrentDevice();
        return ostDevice.getStatus().toLowerCase().equals(OstDevice.CONST_STATUS.AUTHORIZED);
    }

    String getEIP712Hash(String deviceAddress, String deviceManagerAddress) {
        String callData = new GnosisSafe().getAddOwnerWithThresholdExecutableData(deviceAddress);

        int nonce = OstDeviceManager.getById(deviceManagerAddress).getNonce();

        JSONObject safeTxn = new GnosisSafe.SafeTxnBuilder()
                .setCallData(callData)
                .setVerifyingContract(deviceManagerAddress)
                .setToAddress(deviceManagerAddress)
                .setNonce(String.valueOf(nonce))
                .build();

        //EIP-712
        Log.i(TAG, "Performing EIP712 encoding ");
        Log.d(TAG, String.format("String to be encoded  %s", safeTxn.toString()));
        String safeTxnEIP712Hash = null;
        try {
            safeTxnEIP712Hash = new EIP712(safeTxn).toEIP712TransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "EIP-712 error while parsing json object of sageTxn");
            return null;
        }
        return safeTxnEIP712Hash;
    }

    AsyncStatus makeAddDeviceCall(SignedAddDeviceStruct signedAddDeviceStruct) {
        Log.i(TAG, "Api Call payload");
        try {
            String deviceManagerAddress = signedAddDeviceStruct.getDeviceManagerAddress();
            Map<String, Object> map = new OstPayloadBuilder()
                    .setDataDefination(OstDeviceManagerOperation.KIND_TYPE.AUTHORIZE_DEVICE.toUpperCase())
                    .setRawCalldata(signedAddDeviceStruct.getRawCallData())
                    .setCallData(signedAddDeviceStruct.getCallData())
                    .setTo(deviceManagerAddress)
                    .setSignatures(signedAddDeviceStruct.getSignature())
                    .setSigners(Arrays.asList(signedAddDeviceStruct.getSignerAddress()))
                    .setNonce(String.valueOf(signedAddDeviceStruct.getNonce()))
                    .build();
            OstApiClient ostApiClient = new OstApiClient(mUserId);
            JSONObject jsonObject = ostApiClient.postAddDevice(map);
            Log.d(TAG, String.format("JSON Object response: %s", jsonObject.toString()));
            if (isValidResponse(jsonObject)) {

                //increment nonce
                OstDeviceManager.getById(deviceManagerAddress).incrementNonce();

                return new AsyncStatus(true);
            } else {
                return new AsyncStatus(false);
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Exception");
            return new AsyncStatus(false);
        }
    }

    String calculateExpirationHeight(long expiresInSecs) {
        JSONObject jsonObject = null;
        long currentBlockNumber, blockGenerationTime;
        String strCurrentBlockNumber;
        String strBlockGenerationTime;
        try {
            jsonObject = mOstApiClient.getCurrentBlockNumber();
            strCurrentBlockNumber = parseResponseForKey(jsonObject, OstConstants.BLOCK_HEIGHT);
            strBlockGenerationTime = parseResponseForKey(jsonObject, OstConstants.BLOCK_TIME);
        } catch (Throwable e) {
            throw new OstError("wf_bwf_ceh_1", ErrorCode.CHAIN_API_FAILED);
        }

        currentBlockNumber = Long.parseLong(strCurrentBlockNumber);
        blockGenerationTime = Long.parseLong(strBlockGenerationTime);
        long bufferBlocks = (OstConfigs.getInstance().SESSION_BUFFER_TIME) / blockGenerationTime;
        long expiresAfterBlocks = expiresInSecs / blockGenerationTime;
        long expirationHeight = currentBlockNumber + expiresAfterBlocks + bufferBlocks;

        return String.valueOf(expirationHeight);
    }



    // Remove these.
    boolean validatePin(String a, String b) {
        return true;
    }

    AsyncStatus postPinValidated() {
        OstError error = new OstError("bwf_ppv_1", ErrorCode.DEPRECATED);
        return postErrorInterrupt(error);
    }

    AsyncStatus postInvalidPin(OstPinAcceptInterface i_d_k) {
        OstError error = new OstError("bwf_pip_1", ErrorCode.DEPRECATED);
        return postErrorInterrupt(error);
    }
}