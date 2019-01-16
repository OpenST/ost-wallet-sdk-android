package com.ost.ostsdk.utils;

import android.util.Log;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.ostsdk.security.OstCrypto;
import com.ost.ostsdk.security.impls.OstAndroidSecureStorage;
import com.ost.ostsdk.security.impls.OstSdkCrypto;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;

public class KeyGenProcess {

    private static final String TAG = "KeyGenProcess";

    public KeyGenProcess() {
    }

    public String execute(String userId) {

        OstCrypto ostSdkOstCrypto = OstSdkCrypto.getInstance();

        try {

            Log.d(TAG, "Generating Ethereum Keys");
            ECKeyPair ecKeyPair = ostSdkOstCrypto.genECKey(userId);
            Credentials credentials = Credentials.create(ecKeyPair);

            Log.d(TAG, "Extracting Wallet Key");
            byte[] walletKey = credentials.getEcKeyPair().getPrivateKey().toByteArray();

            Log.d(TAG, "Encrypting through TEE");
            byte[] key = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), userId).encrypt(walletKey);

            Log.d(TAG, "Inserting encrypted key from TEE into DB");
            new OstSecureKeyModelRepository().initSecureKey(credentials.getAddress(), key);

            return credentials.getAddress();

        } catch (Exception exception) {
            throw new RuntimeException("Not able to encrypt wallet key :: Reason :" + exception.getMessage());
        }

    }
}