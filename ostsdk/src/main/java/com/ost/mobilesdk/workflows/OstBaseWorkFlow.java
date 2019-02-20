package com.ost.mobilesdk.workflows;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstToken;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Future;

import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;

abstract class OstBaseWorkFlow {
    private static final String TAG = "OstBaseWorkFlow";

    final String mUserId;
    final Handler mHandler;
    final OstWorkFlowCallback mCallback;
    final OstApiClient mOstApiClient;

    /**
     * @Depricated
     * @param userId
     * @param handler
     * @param callback
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

    void postFlowComplete() {
        Log.i(TAG, "Flow complete");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.flowComplete(new OstContextEntity());
            }
        });
    }

    /**
     * @Deprecated: Use postErrorInterrupt instead.
     * @param msg
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

    /**
     * calls flowInterrupt with error message.
     * @param msg: Error Message.
     */
    AsyncStatus postErrorInterrupt(String msg) {
        Log.i(TAG, "Flow Error");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.flowInterrupt( new OstError(msg, getWorkflowType() ) );
            }
        });
        return new AsyncStatus(false);
    }


    AsyncStatus postErrorInterrupt(String internalErrCode, OstErrors.ErrorCode errorCode) {
        Log.i(TAG, "Flow Error");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.flowInterrupt( new OstError(internalErrCode, errorCode, getWorkflowType() ) );
            }
        });
        return new AsyncStatus(false);
    }

    boolean hasCreatedDevice() {
        OstDevice ostDevice = OstUser.getById(mUserId).getCurrentDevice();
        return hasCreatedDevice(ostDevice);
    }

    boolean hasCreatedDevice(OstDevice ostDevice) {
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        return ostKeyManager.getApiKeyAddress().equalsIgnoreCase(ostDevice.getPersonalSignAddress())
                && (OstDevice.CONST_STATUS.CREATED.equals(ostDevice.getStatus().toLowerCase()));
    }

    boolean canDeviceMakeApiCall(OstDevice ostDevice) {
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        boolean isRegistered = ostKeyManager.getApiKeyAddress().equalsIgnoreCase(ostDevice.getPersonalSignAddress());
        isRegistered = isRegistered && ostDevice.canMakeApiCall();
        return isRegistered;
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
        if ( canDeviceMakeApiCall(ostDevice) ) {
            mCurrentDevice = ostDevice;
            return new AsyncStatus(true);
        }
        Log.i(TAG, "Device is not registered");
        return postErrorInterrupt("wp_base_lcd_1" , ErrorCode.DEVICE_UNREGISTERED);
    }

    OstUser mOstUser;
    AsyncStatus loadUser() {
        //Check if we have user information.
        mOstUser = OstUser.getById(mUserId);
        if ( null == mOstUser || TextUtils.isEmpty( mOstUser.getTokenHolderAddress() ) ) {
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
            return postErrorInterrupt("wp_base_lusr_1" , ErrorCode.USER_API_FAILED);
        }
        return new AsyncStatus(true);
    }

    OstToken mOstToken;
    AsyncStatus loadToken(){
        if ( null == mOstUser ) {
            AsyncStatus loadUserStatus = this.loadUser();
            if ( !loadUserStatus.isSuccess() ) {
                return loadUserStatus;
            }
        }

        //Check if we have user information.
        String tokenId = mOstUser.getTokenId();
        mOstToken = OstToken.getById(tokenId);
        if ( null == mOstToken || TextUtils.isEmpty( mOstToken.getChainId() ) ) {
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

        if (null == mOstToken || TextUtils.isEmpty(mOstToken.getChainId()) ) {
            Log.e(TAG, "Token is null or does not contain chainId");
            return postErrorInterrupt("wp_base_ltkn_1" , ErrorCode.TOKEN_API_FAILED);
        }
        return new AsyncStatus(true);
    }


}