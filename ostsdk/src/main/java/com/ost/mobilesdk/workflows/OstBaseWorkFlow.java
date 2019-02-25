package com.ost.mobilesdk.workflows;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;
import com.ost.mobilesdk.utils.EIP712;
import com.ost.mobilesdk.utils.GnosisSafe;
import com.ost.mobilesdk.utils.OstPayloadBuilder;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstPollingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

abstract class OstBaseWorkFlow {
    private static final String TAG = "OstBaseWorkFlow";

    final String mUserId;
    final Handler mHandler;
    final OstWorkFlowCallback mCallback;
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
        mHandler = new Handler();
        mCallback = callback;
        mOstApiClient = new OstApiClient(mUserId);
    }

    boolean hasValidParams() {
        return !TextUtils.isEmpty(mUserId) && null != mHandler && null != mCallback;
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

    AsyncStatus postFlowComplete() {
        Log.i(TAG, "Flow complete");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.flowComplete(new OstWorkflowContext(getWorkflowType()),null);
            }
        });
        return new AsyncStatus(true);
    }

    /**
     * @param msg
     * @Deprecated: Use postErrorInterrupt instead.
     */
    @Deprecated
    void postError(String msg) {
        Log.i(TAG, "Flow Error");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.flowInterrupt(new OstWorkflowContext(getWorkflowType()), new OstError(msg));
            }
        });
    }

    AsyncStatus postGetPin(OstPinAcceptInterface pinAcceptInterface) {
        Log.i(TAG, "get Pin");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.getPin(mUserId, pinAcceptInterface);
            }
        });
        return new AsyncStatus(true);
    }

    AsyncStatus postInvalidPin(OstPinAcceptInterface pinAcceptInterface) {
        Log.i(TAG, "Invalid Pin");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.invalidPin(mUserId, pinAcceptInterface);
            }
        });
        return new AsyncStatus(true);
    }

    AsyncStatus postPinValidated() {
        Log.i(TAG, "Pin validated");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.pinValidated(mUserId);
            }
        });
        return new AsyncStatus(true);
    }

    /**
     * calls flowInterrupt with error message.
     *
     * @param msg: Error Message.
     */
    AsyncStatus postErrorInterrupt(String msg) {
        Log.i(TAG, "Flow Error");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.flowInterrupt(new OstWorkflowContext(getWorkflowType()), new OstError(msg));
            }
        });
        return new AsyncStatus(false);
    }


    AsyncStatus postErrorInterrupt(String internalErrCode, OstErrors.ErrorCode errorCode) {
        Log.i(TAG, "Flow Error");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.flowInterrupt(new OstWorkflowContext(getWorkflowType()), new OstError(internalErrCode, errorCode));
            }
        });
        return new AsyncStatus(false);
    }

    void postRequestAcknowledge(OstWorkflowContext workflowContext, OstContextEntity ostContextEntity) {
        Log.i(TAG, "Request Acknowledge");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.requestAcknowledged(workflowContext, ostContextEntity);
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


    protected String getSalt() {
        String salt = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = mOstApiClient.getSalt();
        } catch (IOException e) {
            Log.e(TAG, "IOException");
        }
        salt = parseResponseForKey(jsonObject, OstConstants.SCRYPT_SALT);
        return salt;
    }

    OstDevice mCurrentDevice;

    AsyncStatus loadCurrentDevice() {
        OstDevice ostDevice = OstUser.getById(mUserId).getCurrentDevice();
        if (canDeviceMakeApiCall(ostDevice)) {
            mCurrentDevice = ostDevice;
            return new AsyncStatus(true);
        }
        Log.i(TAG, "Device is not registered");
        return postErrorInterrupt("wp_base_lcd_1", ErrorCode.DEVICE_UNREGISTERED);
    }

    OstUser mOstUser;

    AsyncStatus loadUser() {
        //Check if we have user information.
        mOstUser = OstUser.getById(mUserId);
        if (null == mOstUser || TextUtils.isEmpty(mOstUser.getTokenHolderAddress())) {
            try {
                OstSdk.updateWithApiResponse(mOstApiClient.getUser());
                mOstUser = OstUser.getById(mUserId);
            } catch (JSONException e) {
                Log.i(TAG, "Encountered JSONException while fetching user.");
                mOstUser = null;
            } catch (IOException e) {
                Log.i(TAG, "Encountered IOException while fetching user.");
                mOstUser = null;
            }
        }

        if (null == mOstUser) {
            Log.i(TAG, "User does not exist");
            return postErrorInterrupt("wp_base_lusr_1", ErrorCode.USER_API_FAILED);
        }
        return new AsyncStatus(true);
    }

    OstToken mOstToken;

    AsyncStatus loadToken() {
        if (null == mOstUser) {
            AsyncStatus loadUserStatus = this.loadUser();
            if (!loadUserStatus.isSuccess()) {
                return loadUserStatus;
            }
        }

        //Check if we have user information.
        String tokenId = mOstUser.getTokenId();
        mOstToken = OstToken.getById(tokenId);
        if (null == mOstToken || TextUtils.isEmpty(mOstToken.getChainId())) {
            //Make API Call.
            try {
                OstSdk.updateWithApiResponse(mOstApiClient.getToken());
                mOstToken = OstToken.getById(tokenId);
            } catch (JSONException e) {
                Log.i(TAG, "Encountered JSONException while fetching token.");
                mOstToken = null;
            } catch (IOException e) {
                Log.i(TAG, "Encountered IOException while fetching token.");
                mOstToken = null;
            }
        }

        if (null == mOstToken || TextUtils.isEmpty(mOstToken.getChainId())) {
            Log.e(TAG, "Token is null or does not contain chainId");
            return postErrorInterrupt("wp_base_ltkn_1", ErrorCode.TOKEN_API_FAILED);
        }
        return new AsyncStatus(true);
    }

    OstRule[] mOstRules;

    protected AsyncStatus loadRules() {
        if (null == mOstUser) {
            AsyncStatus loadUserStatus = this.loadUser();
            AsyncStatus loadTokenStatus = this.loadToken();
            if (!loadTokenStatus.isSuccess() || !loadUserStatus.isSuccess()) {
                return loadUserStatus;
            }
        }
        OstToken ostToken = OstToken.getById(mOstUser.getTokenId());
        mOstRules = ostToken.getAllRules();
        if (null == mOstRules || mOstRules.length == 0) {
            try {
                OstSdk.updateWithApiResponse(mOstApiClient.getAllRules());
                mOstRules = ostToken.getAllRules();
            } catch (JSONException e) {
                Log.i(TAG, "Encountered JSONException while fetching rules.");
                mOstRules = null;
            } catch (IOException e) {
                Log.i(TAG, "Encountered IOException while fetching rules.");
                mOstRules = null;
            }
        }

        if (null == mOstRules) {
            Log.e(TAG, "Rules is null ");
            return postErrorInterrupt("wp_base_lrskn_1", ErrorCode.TOKEN_API_FAILED);
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
                    Log.d(TAG, String.format("Got update message from polling service for device id:%s", entityId));
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
            countDownLatch.await(Integer.MAX_VALUE, TimeUnit.SECONDS);
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

    boolean validatePin(String uPin, String appSalt) {
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        boolean isValidated = ostKeyManager.validatePin(uPin, appSalt);
        return isValidated;
    }

    String getEIP712Hash(String deviceAddress, String deviceManagerAddress) {
        String callData = new GnosisSafe().getAddOwnerWithThresholdExecutableData(deviceAddress);

        int nonce = OstDeviceManager.getById(deviceManagerAddress).getNonce();

        JSONObject safeTxn = new GnosisSafe.SafeTxnBuilder()
                .setAddOwnerExecutableData(callData)
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

    AsyncStatus makeAddDeviceCall(String signature, String signerAddress, String deviceManagerAddress, String deviceAddress) {
        String callData = new GnosisSafe().getAddOwnerWithThresholdExecutableData(deviceAddress);
        int nonce = OstDeviceManager.getById(deviceManagerAddress).getNonce();
        Log.i(TAG, "Api Call payload");
        try {

            Map<String, Object> map = new OstPayloadBuilder()
                    .setDataDefination(OstDeviceManagerOperation.KIND_TYPE.AUTHORIZE_DEVICE.toUpperCase())
                    .setRawCalldata(new GnosisSafe().getAddOwnerWithThresholdCallData(deviceAddress))
                    .setCallData(callData)
                    .setTo(deviceManagerAddress)
                    .setSignatures(signature)
                    .setSigners(Arrays.asList(signerAddress))
                    .setNonce(String.valueOf(nonce))
                    .build();
            OstApiClient ostApiClient = new OstApiClient(mUserId);
            JSONObject jsonObject = ostApiClient.postAddDevice(map);
            Log.d(TAG, String.format("JSON Object response: %s", jsonObject.toString()));
            if (isValidResponse(jsonObject)) {
                return new AsyncStatus(true);
            } else {
                return new AsyncStatus(false);
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Exception");
            return new AsyncStatus(false);
        }
    }
}