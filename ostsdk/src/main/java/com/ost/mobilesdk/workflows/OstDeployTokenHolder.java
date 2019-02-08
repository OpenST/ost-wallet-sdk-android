package com.ost.mobilesdk.workflows;

import android.os.Handler;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.security.OstCrypto;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.security.impls.OstAndroidSecureStorage;
import com.ost.mobilesdk.security.impls.OstSdkCrypto;
import com.ost.mobilesdk.utils.DispatchAsync;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;

public class OstDeployTokenHolder implements OstDeviceRegisteredInterface {

    private static final String TAG = "IDPFlow";
    private static final int THREE_TIMES = 3;
    private final String mUserId;
    private final String mTokenId;
    private final String mPassWord;
    private final boolean mIsBiometricNeeded;
    private final Handler mHandler;
    private final OstWorkFlowCallback mCallback;
    private final String mUPin;

    public OstDeployTokenHolder(String userId, String tokenId, String uPin, String password, boolean isBiometricNeeded, Handler handler, OstWorkFlowCallback callback) {
        mUserId = userId;
        mTokenId = tokenId;
        mUPin = uPin;
        mPassWord = password;
        mIsBiometricNeeded = isBiometricNeeded;
        mHandler = handler;
        mCallback = callback;
    }

    public void init(JSONObject payload, String signature) throws Exception {
        String userId = "", passPhrase = "", scyrptSalt = "", hkdfSalt = "";

        Log.d(TAG, "Generate encrypted keys");
        OstCrypto ostSdkOstCrypto = OstSdkCrypto.getInstance();

        byte[] encryptedKey;
        try {

            Log.d(TAG, "Generating Ethereum Keys");
            ECKeyPair ecKeyPair = ostSdkOstCrypto.genECKey();
            Credentials credentials = Credentials.create(ecKeyPair);

            Log.d(TAG, "Extracting Wallet Key");
            byte[] walletKey = credentials.getEcKeyPair().getPrivateKey().toByteArray();

            Log.d(TAG, "Encrypting through TEE");
            byte[] key = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), userId).encrypt(walletKey);

            Log.d(TAG, "Inserting encrypted key from TEE into DB");
            new OstSecureKeyModelRepository().initSecureKey(credentials.getAddress(), key);

            Log.d(TAG, "Generating SCyrpt key using passPhrase and salt");
            byte[] scryptKey = ostSdkOstCrypto.genSCryptKey(passPhrase.getBytes(), scyrptSalt.getBytes());

            Log.d(TAG, "Generating HKDF key from SCyrpt Key");
            byte[] hkdfKey = ostSdkOstCrypto.genHKDFKey(scryptKey, hkdfSalt.getBytes());

            Log.d(TAG, "Generating hkdf hash as AEAD from HKDF Key");
            byte[] hkdfHash = ostSdkOstCrypto.genDigest(hkdfKey);

            Log.d(TAG, "Encrypting Wallet key from scrypt key");
            encryptedKey = ostSdkOstCrypto.aesEncryption(scryptKey, walletKey, hkdfHash);
        } catch (Exception exception) {
            throw new RuntimeException("Not able to encrypt wallet key :: Reason :" + exception.getMessage());
        }


        Log.d(TAG, "Post encrypted keys to kit");
        // Todo:: post key
        Log.d(TAG, "Parsing kit response");

    }

    public void perform() {
        DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public void execute() {
                if (hasAuthorizedDevice()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.flowComplete(new OstContextEntity());
                        }
                    });
                } else {
                    //Todo :: get salt from Kit /users/{user_id}/recovery-keys

                    String salt = "";
                    String recoveryAddress = createRecoveryKey(salt);

                    String sessionAddress = new OstKeyManager(mUserId).createSessionKey();
                    String expirationHeight = "100000";
                    String spendingLimit = "100000";
                    JSONObject response;
                    try {
                        response = new OstApiClient(mUserId).postTokenDeployment(sessionAddress, expirationHeight, spendingLimit);
                    } catch (IOException e) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mCallback.flowInterrupt(new OstError("IOException"));
                            }
                        });
                    }



                    //Todo :: backup  recoveryAddress on kit. calls endpoint /devices/back-up

                    //Todo :: if Device Manager already exists, add recovery key to Device Manager.
                    //Todo :: If not deploy Device Manager and add recovery key to Device Manager
                }
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
    public void cancelFlow(String cancelReason) {

    }

    @Override
    public void deviceRegistered(JSONObject apiResponse) {

    }
}