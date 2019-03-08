package com.ost.mobilesdk.workflows;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.biometric.OstBiometricAuthentication;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstDeviceManager;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;
import com.ost.mobilesdk.models.entities.OstRule;
import com.ost.mobilesdk.models.entities.OstToken;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.ecKeyInteracts.OstKeyManager;
import com.ost.mobilesdk.ecKeyInteracts.structs.SignedAddDeviceStruct;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;
import com.ost.mobilesdk.utils.EIP712;
import com.ost.mobilesdk.utils.GnosisSafe;
import com.ost.mobilesdk.utils.OstPayloadBuilder;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstPollingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

abstract class OstBaseWorkFlow {
    private static final String TAG = "OstBaseWorkFlow";

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
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                return process();
            }
        });
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

    void postVerifyData(OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {
        Log.i(TAG, "Post Verify data");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OstWorkFlowCallback callback = getCallback();
                if ( null != callback ) {
                    callback.verifyData(new OstWorkflowContext(getWorkflowType()), ostContextEntity, ostVerifyDataInterface);
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

    boolean hasValidAddress(String address) {
        OstDevice ostDevice = OstDevice.getById(address);
        if (null != ostDevice) return true;
        try {
            mOstApiClient.getDevice(address);
        } catch (IOException e) {
            Log.e(TAG, "Exception while getting device");
        }
        ostDevice = OstDevice.getById(address);
        return (null != ostDevice);
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
        String deviceAddress = ostDevice.getAddress();
        if ( null == ostDevice) {
            throw new OstError("wp_base_apic_2", ErrorCode.DEVICE_NOT_SETUP);
        }
        else if ( !canDeviceMakeApiCall(ostDevice) ) {

            // Lets try and make an api call.
            hasSyncedDeviceToEnsureApiCommunication = true;
            try {
                syncCurrentDevice();
            } catch (OstError ostError) {
                //We know this could happen. Lets ignore the error given by syncCurrentDevice.
                throw new OstError("wp_base_apic_3", ErrorCode.DEVICE_NOT_SETUP);
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
        if (null == mOstToken || TextUtils.isEmpty(mOstToken.getChainId())) {
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
        String currentDeviceAddress = device.getAddress();
        try {
            mOstApiClient.getDevice( currentDeviceAddress );
        } catch (IOException e) {
            throw new OstError("wp_base_scd_2", ErrorCode.GET_DEVICE_API_FAILED);
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
    OstRule[] ensureOstRules()  throws OstError {
        if ( null == mOstToken ) {  ensureOstToken(); }

        mOstRules = mOstToken.getAllRules();
        if (null == mOstRules || mOstRules.length == 0) {
            try {
                mOstApiClient.getAllRules();
            } catch (IOException e) {
                OstError ostError = new OstError("wp_base_eot_1", ErrorCode.RULES_API_FAILED);
                throw ostError;
            }
            mOstRules = mOstToken.getAllRules();
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

    @Deprecated
    protected AsyncStatus loadRules() {
        try {
            ensureOstRules();
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        }
        return new AsyncStatus(true);
    }

    Bundle waitForUpdate(final String pEntityType, final String pEntityId) {
        final Bundle bundle = new Bundle();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        BroadcastReceiver updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                Log.d(TAG, "Intent received");
                String userId = intent.getStringExtra(OstPollingService.EXTRA_USER_ID);
                String entityId = intent.getStringExtra(OstPollingService.EXTRA_ENTITY_ID);
                String entityType = intent.getStringExtra(OstPollingService.EXTRA_ENTITY_TYPE);
                boolean isValidResponse = intent.getBooleanExtra(OstPollingService.EXTRA_IS_VALID_RESPONSE, true);
                boolean isPollingTimeOut = intent.getBooleanExtra(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true);
                if (mUserId.equals(userId) && pEntityType.equalsIgnoreCase(entityType) && pEntityId.equals(entityId)) {
                    Log.d(TAG, String.format("Got update message from polling service for %s id:%s", entityType, entityId));
                    if (isPollingTimeOut) {
                        Log.w(TAG, "Polling timeout reached");
                    }
                    if (!isValidResponse) {
                        Log.w(TAG, "Not a valid response");
                    }
                    bundle.putAll(intent.getExtras());
                    countDownLatch.countDown();
                }
            }
        };
        LocalBroadcastManager.getInstance(OstSdk.getContext()).registerReceiver(updateReceiver,
                new IntentFilter(OstPollingService.ENTITY_UPDATE_MESSAGE));
        try {
            countDownLatch.await(OstConstants.POLLING_WAIT_TIME_IN_SECS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "Unexpected error while waiting for polling", e);
        }
        LocalBroadcastManager.getInstance(OstSdk.getContext()).unregisterReceiver(updateReceiver);
        return bundle;
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