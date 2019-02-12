package com.ost.mobilesdk.workflows;

import android.os.Handler;
import android.util.Log;

import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.security.impls.OstSdkCrypto;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

import java.io.IOException;

public class OstDeployTokenHolder implements OstDeviceRegisteredInterface {

    private static final String TAG = "IDPFlow";
    private static final int THREE_TIMES = 3;
    private final String mUserId;
    private final String mPassWord;
    private final Handler mHandler;
    private final OstWorkFlowCallback mCallback;
    private final String mUPin;
    private final String mExpirationHeight;
    private final String mSpendingLimit;

    public OstDeployTokenHolder(String userId, String uPin, String password, String expirationHeight, String spendingLimit, Handler handler, OstWorkFlowCallback callback) {
        mUserId = userId;
        mUPin = uPin;
        mPassWord = password;
        mExpirationHeight = expirationHeight;
        mSpendingLimit = spendingLimit;
        mHandler = handler;
        mCallback = callback;
    }

    public void perform() {
        DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                if (hasAuthorizedDevice()) {
                    Log.d(TAG, "Device is authorized");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.flowComplete(new OstContextEntity());
                        }
                    });
                } else {
                    //Todo :: get salt from Kit /users/{user_id}/recovery-keys
                    //Todo :: backup  recoveryAddress on kit. calls endpoint /devices/back-up
                    /****************Deferred code*****************/
                    String salt = "salt";
                    String recoveryAddress = createRecoveryKey(salt);
                    /*********************************************/

                    String sessionAddress = new OstKeyManager(mUserId).createSessionKey();

                    JSONObject response;
                    try {
                        response = new OstApiClient(mUserId).postTokenDeployment(sessionAddress, mExpirationHeight, mSpendingLimit);

                    } catch (IOException e) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mCallback.flowInterrupt(new OstError("IOException"));
                            }
                        });
                    }
                    //Todo:: parse response and wait for activation

                }
                return new AsyncStatus(true);
            }
        });
    }

    private String createRecoveryKey(String salt) {
        byte[] hashPassword = OstSdkCrypto.getInstance().genDigest(mPassWord.getBytes(), THREE_TIMES);
        byte[] scryptInput = ((new String(hashPassword)) + mUPin + mUserId).getBytes();
        byte[] seed = OstSdkCrypto.getInstance().genSCryptKey(scryptInput, salt.getBytes());

        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        //Don't store key of recovery key
        String address = ostKeyManager.createHDKey(seed);
        return address;
    }

    private boolean hasAuthorizedDevice() {
        OstDevice[] ostDevices = OstDevice.getDevicesByParentId(mUserId);
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        for (OstDevice device : ostDevices) {
            if (ostKeyManager.getApiKeyAddress().equalsIgnoreCase(device.getPersonalSignAddress())
                    && OstDevice.CONST_STATUS.AUTHORIZED.equals(device.getStatus())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void cancelFlow(OstError ostError) {

    }

    @Override
    public void deviceRegistered(JSONObject apiResponse) {

    }
}