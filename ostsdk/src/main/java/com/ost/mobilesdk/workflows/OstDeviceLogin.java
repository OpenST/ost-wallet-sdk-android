package com.ost.mobilesdk.workflows;

import android.os.Looper;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.mobilesdk.security.OstCrypto;
import com.ost.mobilesdk.security.impls.OstAndroidSecureStorage;
import com.ost.mobilesdk.security.impls.OstSdkCrypto;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstStartPollingInterface;

import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;

public class OstDeviceLogin implements OstPinAcceptInterface, OstDeviceRegisteredInterface, OstStartPollingInterface {

    private static final String TAG = "ADPFlow";

    public OstDeviceLogin() {

    }

    public void init(JSONObject payload, String signature) throws Exception {
        //Todo:: Condition check:: should not be on Main Thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new Exception("FLow call should not be on Main thread");
        }

        Log.d(TAG, "Kit api call");
        // Todo:: Kit api call for scyrptSalt and hkdfSalt

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

    @Override
    public void startPolling() {

    }

    @Override
    public void cancelFlow(OstError ostError) {

    }

    @Override
    public void deviceRegistered(JSONObject apiResponse) {

    }

    @Override
    public void pinEntered(String uPin, String appUserPassword) {

    }
}