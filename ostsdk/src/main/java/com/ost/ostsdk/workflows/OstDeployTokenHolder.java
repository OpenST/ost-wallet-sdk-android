package com.ost.ostsdk.workflows;

import android.os.Handler;
import android.util.Log;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.ostsdk.models.entities.OstDevice;
import com.ost.ostsdk.security.OstCrypto;
import com.ost.ostsdk.security.OstKeyManager;
import com.ost.ostsdk.security.impls.OstAndroidSecureStorage;
import com.ost.ostsdk.security.impls.OstSdkCrypto;
import com.ost.ostsdk.utils.DispatchAsync;
import com.ost.ostsdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.ostsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;

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
            ECKeyPair ecKeyPair = ostSdkOstCrypto.genECKey(passPhrase/* Todo:: Seed Need to be identified*/);
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