package com.ost.ostsdk.workflows;

import android.os.Looper;
import android.util.Log;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.SecureKeyModelRepository;
import com.ost.ostsdk.security.Crypto;
import com.ost.ostsdk.security.impls.AndroidSecureStorage;
import com.ost.ostsdk.security.impls.OstSdkCrypto;

import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;

import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class InitialDeviceProvisioningFlow {

    private static final String TAG = "IDPFlow";

    public InitialDeviceProvisioningFlow() {

    }

    public void init(JSONObject payload, String signature) throws Exception {
        //Todo:: Condition check:: should not be on Main Thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new Exception("FLow call should not be on Main thread");
        }

        Log.d(TAG, "Kit api call");
        // Todo:: Kit api call for scyrptSalt and hkdfSalt
        Call<Response> responseInitActionCall = OstSdk.getKitNetworkClient().initAction(payload, signature);

        responseInitActionCall.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });

        String userId = "", passPhrase = "", scyrptSalt = "", hkdfSalt = "";

        Log.d(TAG, "Generate encrypted keys");
        Crypto ostSdkCrypto = OstSdkCrypto.getInstance();

        byte[] encryptedKey;
        try {

            Log.d(TAG, "Generating Ethereum Keys");
            ECKeyPair ecKeyPair = ostSdkCrypto.genECKey(passPhrase/* Todo:: Seed Need to be identified*/);
            Credentials credentials = Credentials.create(ecKeyPair);

            Log.d(TAG, "Extracting Wallet Key");
            byte[] walletKey = credentials.getEcKeyPair().getPrivateKey().toByteArray();

            Log.d(TAG, "Encrypting through TEE");
            byte[] key = AndroidSecureStorage.getInstance(OstSdk.getContext(), userId).encrypt(walletKey);

            Log.d(TAG, "Inserting encrypted key from TEE into DB");
            new SecureKeyModelRepository().initSecureKey(credentials.getAddress(), key);

            Log.d(TAG, "Generating SCyrpt key using passPhrase and salt");
            byte[] scryptKey = ostSdkCrypto.genSCryptKey(passPhrase.getBytes(), scyrptSalt.getBytes());

            Log.d(TAG, "Generating HKDF key from SCyrpt Key");
            byte[] hkdfKey = ostSdkCrypto.genHKDFKey(scryptKey, hkdfSalt.getBytes());

            Log.d(TAG, "Generating hkdf hash as AEAD from HKDF Key");
            byte[] hkdfHash = ostSdkCrypto.genDigest(hkdfKey);

            Log.d(TAG, "Encrypting Wallet key from scrypt key");
            encryptedKey = ostSdkCrypto.aesEncryption(scryptKey, walletKey, hkdfHash);
        } catch (Exception exception) {
            throw new RuntimeException("Not able to encrypt wallet key :: Reason :" + exception.getMessage());
        }


        Log.d(TAG, "Post encrypted keys to kit");
        // Todo:: post key
        Call<Response> responsePostKeyCall = OstSdk.getKitNetworkClient().postKey(encryptedKey, signature);

        responsePostKeyCall.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });

        Log.d(TAG, "Parsing kit response");

    }
}