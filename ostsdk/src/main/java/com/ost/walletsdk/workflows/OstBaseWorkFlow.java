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
import com.ost.walletsdk.annotations.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.ost.walletsdk.OstConfigs;
import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.R;
import com.ost.walletsdk.biometric.OstBiometricAuthentication;
import com.ost.walletsdk.ecKeyInteracts.OstKeyManager;
import com.ost.walletsdk.ecKeyInteracts.OstRecoveryManager;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
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

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

abstract class OstBaseWorkFlow implements OstPinAcceptInterface {
    private static final String TAG = "OstBaseWorkFlow";
    
    private final static ThreadPoolExecutor THREAD_POOL_EXECUTOR = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(1);

    //region - Variables
    OstApiClient mOstApiClient;
    final String mUserId;
    final Handler mHandler;
    final boolean mShouldPoll;
    final String mWorkflowId;

    WorkflowStateManager stateManager;
    
    private final WeakReference <OstWorkFlowCallback> workFlowCallbackWeakReference;
    private OstBiometricAuthentication.Callback mBioMetricCallBack;
    private int mPinAskCount = 0;
    //endregion
    
    
    //region - Getters
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.UNKNOWN;
    }
    
    protected OstWorkFlowCallback getCallback() {
        return workFlowCallbackWeakReference.get();
    }

    ThreadPoolExecutor getAsyncQueue() {
        return THREAD_POOL_EXECUTOR;
    }
    //endregion
    
    
    //region - Setters
    protected void setStateManager() {
        stateManager = new WorkflowStateManager();
    }

    void initApiClient() {
        mOstApiClient = new OstApiClient(mUserId);
    }
    //endregion


    /**
     * @param userId   - Ost Platform user-id
     * @param callback - callback handler of the application.
     */
    OstBaseWorkFlow(@NonNull String userId, @NonNull OstWorkFlowCallback callback) {
        this(userId, true, callback);
    }

    /**
     * @param userId   - Ost Platform user-id
     * @param callback - callback handler of the application.
     */
    OstBaseWorkFlow(@NonNull String userId, boolean shouldPoll, @NonNull OstWorkFlowCallback callback) {
        mUserId = userId;
        mShouldPoll = shouldPoll;
        mHandler = new Handler(Looper.getMainLooper());
        workFlowCallbackWeakReference = new WeakReference<>(callback);
        this.mWorkflowId = UUID.randomUUID().toString();
        initApiClient();
        setStateManager();
    }

    
    //region - Validators
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

    boolean hasDeviceApiKey(OstDevice ostDevice) {
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        return ostKeyManager.getApiKeyAddress().equalsIgnoreCase(ostDevice.getApiSignerAddress());
    }

    boolean canDeviceMakeApiCall(OstDevice ostDevice) {
        //Must have Device Api Key which should have been registered.
        return hasDeviceApiKey(ostDevice) && ostDevice.canMakeApiCall();
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
    //endregion

    
    //region - SME methods
    public Future<AsyncStatus> perform() {
        return getAsyncQueue().submit(new Callable<AsyncStatus>() {
            @Override
            public AsyncStatus call() {
                return process();
            }
        });
    }

    synchronized protected AsyncStatus process() {
        AsyncStatus status = null;
        String currentState = stateManager.getCurrentState();
        Object currentStateObject = stateManager.getStateObject();
        status = onStateChanged(currentState, currentStateObject);
        if ( null != status ) {
            return status;
        }
        return new AsyncStatus(true);
    }

    protected AsyncStatus onStateChanged(String state, Object stateObject) {
        try {
            switch (state) {
                case WorkflowStateManager.INITIAL:
                    return performValidations(stateObject);

                case WorkflowStateManager.PARAMS_VALIDATED:
                    return performUserDeviceValidation(stateObject);

                case WorkflowStateManager.DEVICE_VALIDATED:
                    Log.i(TAG, "Ask for authentication");
                    if (shouldAskForAuthentication()) {
                        if (shouldAskForBioMetric()) {
                            new OstBiometricAuthentication(OstSdk.getContext(), getBiometricHeading(), getBioMetricCallBack());
                        } else {
                            return goToState(WorkflowStateManager.PIN_AUTHENTICATION_REQUIRED);
                        }
                    } else {
                        return goToState(WorkflowStateManager.AUTHENTICATED);
                    }
                    break;

                case WorkflowStateManager.PIN_AUTHENTICATION_REQUIRED:
                    postGetPin(this);
                    break;

                case WorkflowStateManager.PIN_INFO_RECEIVED:
                    return verifyUserPin( (UserPassphrase) stateObject );

                case WorkflowStateManager.AUTHENTICATED:
                    //Call the abstract method.
                    AsyncStatus status = performOnAuthenticated();
                    if ( !status.isSuccess() ) {
                        //performOnAuthenticated will throw OstApiError. So, this is hypothetical case.
                        goToState(WorkflowStateManager.COMPLETED_WITH_ERROR);
                    }
                    return status;
                case WorkflowStateManager.CANCELLED:
                    if ( stateObject instanceof OstError) {
                        return postErrorInterrupt( (OstError) stateObject );
                    } else {
                        OstError error = new OstError("bua_wf_osc_canceled", ErrorCode.WORKFLOW_CANCELLED);
                        return postErrorInterrupt(error);
                    }

                case WorkflowStateManager.COMPLETED:
                    return new AsyncStatus(true);

                case WorkflowStateManager.COMPLETED_WITH_ERROR:
                    return new AsyncStatus(false);
                case WorkflowStateManager.CALLBACK_LOST:
                    Log.w(TAG, "The callback instance has been lost. Workflow class name: " + getClass().getName());
                    return new AsyncStatus(false);
            }
        } catch (OutOfMemoryError outOfMemoryError) {
            OstError ostError = new OstError("bua_wf_osc_outofmemory", ErrorCode.OUT_OF_MEMORY_ERROR);
            return postErrorInterrupt(ostError);
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        } catch (Throwable throwable) {
            OstError ostError = new OstError("bua_wf_osc_1", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            ostError.setStackTrace(throwable.getStackTrace());
            return postErrorInterrupt(ostError);
        }
        return new AsyncStatus(true);
    }
    //endregion


    //region - SME helper methods
    protected AsyncStatus performNext(Object stateObject) {
        stateManager.setNextState(stateObject);
        return process();
    }

    protected AsyncStatus goToState(String state, Object stateObject) {
        stateManager.setState(state, stateObject);
        return process();
    }

    protected void performWithState(String state, Object stateObject) {
        stateManager.setState(state, stateObject);
        perform();
    }

    //Helpers.
    protected AsyncStatus goToState(String state) {
        return goToState(state, null);
    }
    protected AsyncStatus performNext() {
        return performNext(null);
    }
    protected void performWithState(String state) {
        performWithState(state, null);
    }
    //endregion


    //region - Entity ensure methods
    protected AsyncStatus performValidations(Object stateObject) {
        try {
            ensureValidParams();
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        }
        return performNext();
    }

    protected AsyncStatus performUserDeviceValidation(Object stateObject) {

        try {
            //Ensure sdk can make Api calls
            ensureApiCommunication();

            // Ensure we have OstUser complete entity.
            ensureOstUser( shouldAskForAuthentication() );

            // Ensure we have OstToken complete entity.
            ensureOstToken();

            if ( shouldCheckCurrentDeviceAuthorization() ) {
                //Ensure Device is Authorized.
                ensureDeviceAuthorized();

                //Ensures Device Manager is present as derived classes are likely going to need nonce.
                ensureDeviceManager();
            }

        } catch (OstError err) {
            return postErrorInterrupt(err);
        }

        return onUserDeviceValidationPerformed(stateObject);
    }

    OstDevice mCurrentDevice;
    private boolean hasSyncedDeviceToEnsureApiCommunication = false;
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
            syncOstUser();
        }
    }

    void syncOstUser() throws OstError {
        mOstApiClient.getUser();
        mOstUser = OstUser.getById(mUserId);
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
            syncOstToken();
        }
    }

    void syncOstToken() {
        mOstApiClient.getToken();
        mOstToken = OstToken.getById(mOstUser.getTokenId());
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
        mOstApiClient.getDevice( currentDeviceAddress );
        mCurrentDevice = ostUser.getCurrentDevice();
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

        mOstApiClient.getDeviceManager();
        mDeviceManager = OstDeviceManager.getById( deviceManagerAddress );
        return mDeviceManager;
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
        JSONObject rulesResponseObject = mOstApiClient.getAllRules();
        try {
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
            throw OstError.ApiResponseError("wp_base_eot_1", "getAllRules", rulesResponseObject);
        }
        return mOstRules;
    }
    //endregion


    //region - Flow method
    protected boolean shouldAskForAuthentication() {
        return true;
    }
    boolean shouldCheckCurrentDeviceAuthorization() {
        return true;
    }
    //endregion


    //region - Pin flow methods
    @Override
    public void pinEntered(UserPassphrase passphrase) {
        performWithState(WorkflowStateManager.PIN_INFO_RECEIVED, passphrase);

    }

    AsyncStatus verifyUserPin(UserPassphrase passphrase) {

        OstRecoveryManager recoveryManager = new OstRecoveryManager(mUserId);
        boolean isValid = recoveryManager.validatePassphrase(passphrase);

        if ( isValid ) {
            postPinValidated();
            recoveryManager = null;
            return goToState(WorkflowStateManager.AUTHENTICATED);
        }

        mPinAskCount = mPinAskCount + 1;
        if (mPinAskCount < OstConfigs.getInstance().getPIN_MAX_RETRY_COUNT()) {
            Log.d(TAG, "Pin InValidated ask for pin again");
            OstPinAcceptInterface me = this;
            return postInvalidPin(me);
        }
        Log.d(TAG, "Max pin ask limit reached");
        return postErrorInterrupt("bpawf_vup_2", ErrorCode.MAX_PASSPHRASE_VERIFICATION_LIMIT_REACHED);
    }

    AsyncStatus postPinValidated() {
        Log.i(TAG, "Pin validated");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OstWorkFlowCallback callback = getCallback();
                if ( null != callback ) {
                    callback.pinValidated(getWorkflowContext(), mUserId);
                }
            }
        });
        return new AsyncStatus(true);
    }

    AsyncStatus postInvalidPin(OstPinAcceptInterface pinAcceptInterface) {
        Log.i(TAG, "Invalid Pin");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OstWorkFlowCallback callback = getCallback();
                if ( null != callback ) {
                    callback.invalidPin(getWorkflowContext(), mUserId, pinAcceptInterface);
                } else {
                    goToState(WorkflowStateManager.CALLBACK_LOST);
                }
            }
        });
        return new AsyncStatus(true);
    }

    public void cancelFlow() {
        performWithState(WorkflowStateManager.CANCELLED);
    }
    //endregion


    //region - Post methods for app
    AsyncStatus postGetPin(OstPinAcceptInterface pinAcceptInterface) {
        Log.i(TAG, "get Pin");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OstWorkFlowCallback callback = getCallback();
                if ( null != callback ) {
                    callback.getPin(getWorkflowContext(), mUserId, pinAcceptInterface);
                } else {
                    goToState(WorkflowStateManager.CALLBACK_LOST);
                }
            }
        });
        return new AsyncStatus(true);
    }
    AsyncStatus postFlowComplete(OstContextEntity ostContextEntity) {
        Log.i(TAG, "Flow complete");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OstWorkFlowCallback callback = getCallback();
                if ( null != callback ) {
                    callback.flowComplete(getWorkflowContext(), ostContextEntity);
                }
            }
        });
        return new AsyncStatus(true);
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
                    callback.flowInterrupt(getWorkflowContext(), error);
                }
            }
        });
        return new AsyncStatus(false);
    }

    void postRequestAcknowledge(OstContextEntity ostContextEntity) {
        OstWorkflowContext workflowContext = getWorkflowContext();
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
                            getWorkflowContext(),
                            ostContextEntity,
                            ostVerifyDataInterface
                    );
                }
            }
        });
    }
    //endregion


    //region - Biometric methods
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

    void onBioMetricAuthenticationSuccess() {
        performWithState(WorkflowStateManager.AUTHENTICATED);
    }

    void onBioMetricAuthenticationFail() {
        //Ask for pin.
        performWithState(WorkflowStateManager.PIN_AUTHENTICATION_REQUIRED);
    }

    boolean shouldAskForBioMetric() {
        return OstSdk.isBiometricEnabled(mUserId)
                && isBioMetricEnabled();
    }

    boolean isBioMetricEnabled() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) OstSdk.getContext()
                    .getSystemService(Context.FINGERPRINT_SERVICE);
            return null != fingerprintManager && fingerprintManager.isHardwareDetected()
                    && fingerprintManager.hasEnrolledFingerprints();
        }
        return false;
    }
    //endregion


    //region - Perform methods
    AsyncStatus performOnAuthenticated() {
        return new AsyncStatus(true);
    }
    protected AsyncStatus onUserDeviceValidationPerformed(Object stateObject) {
        return performNext();
    }
    //endregion

    //region - Helper methods
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
    }

    String calculateExpirationHeight(long expiresInSecs) {
        JSONObject jsonObject = mOstApiClient.getCurrentBlockNumber();
        long currentBlockNumber, blockGenerationTime;
        String strCurrentBlockNumber;
        String strBlockGenerationTime;
        try {
            strCurrentBlockNumber = parseResponseForKey(jsonObject, OstConstants.BLOCK_HEIGHT);
            strBlockGenerationTime = parseResponseForKey(jsonObject, OstConstants.BLOCK_TIME);
        } catch (Throwable e) {
            throw OstError.ApiResponseError("wf_bwf_ceh_1", "getCurrentBlockNumber", jsonObject);
        }

        currentBlockNumber = Long.parseLong(strCurrentBlockNumber);
        blockGenerationTime = Long.parseLong(strBlockGenerationTime);
        long bufferBlocks = (OstConfigs.getInstance().getSESSION_BUFFER_TIME()) / blockGenerationTime;
        long expiresAfterBlocks = expiresInSecs / blockGenerationTime;
        long expirationHeight = currentBlockNumber + expiresAfterBlocks + bufferBlocks;

        return String.valueOf(expirationHeight);
    }

    String getBiometricHeading() {
        return new CommonUtils().getStringRes(R.string.authorize);
    }

    OstWorkflowContext getWorkflowContext() {
        return new OstWorkflowContext(this.mWorkflowId, this.getWorkflowType());
    }


    //endregion
}