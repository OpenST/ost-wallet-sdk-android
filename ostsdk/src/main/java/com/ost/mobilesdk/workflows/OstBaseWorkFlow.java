package com.ost.mobilesdk.workflows;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.biometric.OstBiometricAuthentication;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstToken;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.mobilesdk.workflows.services.OstPollingService;

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

    public OstConstants.WORKFLOW_TYPE getWorkflowType() {
        return OstConstants.WORKFLOW_TYPE.UNKNOWN;
    }

    AsyncStatus postFlowComplete() {
        Log.i(TAG, "Flow complete");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.flowComplete(new OstContextEntity());
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
                mCallback.flowInterrupt(new OstError(msg));
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
                mCallback.flowInterrupt(new OstError(msg, getWorkflowType()));
            }
        });
        return new AsyncStatus(false);
    }


    AsyncStatus postErrorInterrupt(String internalErrCode, OstErrors.ErrorCode errorCode) {
        Log.i(TAG, "Flow Error");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.flowInterrupt(new OstError(internalErrCode, errorCode, getWorkflowType()));
            }
        });
        return new AsyncStatus(false);
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
                OstSdk.parse(mOstApiClient.getUser());
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
                OstSdk.parse(mOstApiClient.getToken());
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

    boolean waitForUpdate(final String pEntityType, final String pEntityId) {
        final boolean[] isTimeout = new boolean[1];
        isTimeout[0] = false;

        CountDownLatch countDownLatch = new CountDownLatch(1);
        BroadcastReceiver updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                Log.d(TAG, "Intent received");
                String userId = intent.getStringExtra(OstPollingService.EXTRA_USER_ID);
                String entityId = intent.getStringExtra(OstPollingService.EXTRA_ENTITY_ID);
                String entityType = intent.getStringExtra(OstPollingService.EXTRA_ENTITY_TYPE);
                boolean isPollingTimeOut = intent.getBooleanExtra(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true);
                if (mUserId.equals(userId) && pEntityType.equalsIgnoreCase(entityType) && pEntityId.equals(entityId)) {
                    Log.d(TAG, String.format("Got update message from polling service for device id:%s", entityId));
                    if (isPollingTimeOut) {
                        Log.w(TAG, "Polling timeout reached");
                        isTimeout[0] = true;
                    }
                    countDownLatch.countDown();
                }
            }
        };
        LocalBroadcastManager.getInstance(OstSdk.getContext()).registerReceiver(updateReceiver,
                new IntentFilter(OstPollingService.ENTITY_UPDATE_MESSAGE));
        try {
            countDownLatch.await(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(OstSdk.getContext()).unregisterReceiver(updateReceiver);
        return isTimeout[0];
    }

    boolean shouldAskForBioMetric() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) OstSdk.getContext()
                    .getSystemService(Context.FINGERPRINT_SERVICE);
            if (null != fingerprintManager && fingerprintManager.isHardwareDetected()
                    && fingerprintManager.hasEnrolledFingerprints()) {
                return true;
            }
        }
        return false;
    }

}