package com.ost.mobilesdk.workflows;

import android.os.Handler;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.security.impls.OstSdkCrypto;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstUserPollingService;

import org.json.JSONObject;

public class OstActivateUser extends OstBaseWorkFlow {

    private static final String TAG = "IDPFlow";
    private static final int THREE_TIMES = 3;
    private final String mPassWord;
    private final String mUPin;
    private final String mExpirationHeight;
    private final String mSpendingLimit;

    public OstActivateUser(String userId, String uPin, String password, String expirationHeight,
                           String spendingLimit, Handler handler, OstWorkFlowCallback callback) {
        super(userId, handler, callback);
        mUPin = uPin;
        mPassWord = password;
        mExpirationHeight = expirationHeight;
        mSpendingLimit = spendingLimit;
    }

    @Override
    protected AsyncStatus process() {

        if (!hasAuthorizedDevice()) {
            Log.i(TAG, "Device is not authorized");
            postError("Device is not authorized");
            return new AsyncStatus(false);
        } else if (hasActivatedUser()) {
            Log.i(TAG, "User is already activated");
            postFlowComplete();
        } else {
            //Todo :: get salt from Kit /users/{user_id}/recovery-keys
            //Todo :: backup  recoveryAddress on kit. calls endpoint /devices/back-up
            /****************Deferred code*****************/
            String salt = "salt";
            String recoveryAddress = createRecoveryKey(salt);
            /*********************************************/

            Log.i(TAG, "Creating session key");
            String sessionAddress = new OstKeyManager(mUserId).createSessionKey();

            Log.i(TAG, "Deploying token holder");
            Log.d(TAG, String.format("Deploying token with SessionAddress: %s, ExpirationHeight: %s," +
                            " SpendingLimit: %s, RecoveryAddress: %s", sessionAddress,
                    mExpirationHeight, mSpendingLimit, recoveryAddress));
            JSONObject response;
            try {
                response = new OstApiClient(mUserId).postTokenDeployment(sessionAddress,
                        mExpirationHeight, mSpendingLimit, recoveryAddress);
                OstSdk.parse(response);
            } catch (Exception e) {
                postError("Exception in post token deployment");
                return new AsyncStatus(false);
            }

            Log.i(TAG, "Response received for post Token deployment");
            postFlowComplete();

            Log.i(TAG, "Starting user polling service");
            OstUserPollingService.startPolling(mUserId, mUserId, OstUser.CONST_STATUS.ACTIVATING,
                    OstUser.CONST_STATUS.ACTIVATED);


        }
        return new AsyncStatus(true);
    }

    private boolean hasActivatedUser() {
        return OstUser.CONST_STATUS.ACTIVATED.equals(OstSdk.getUser(mUserId).getStatus());
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
        OstDevice ostDevice = OstSdk.getUser(mUserId).getCurrentDevice();
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        return ostKeyManager.getApiKeyAddress().equalsIgnoreCase(ostDevice.getPersonalSignAddress())
                && (OstDevice.CONST_STATUS.AUTHORIZED.equals(ostDevice.getStatus()));
    }
}