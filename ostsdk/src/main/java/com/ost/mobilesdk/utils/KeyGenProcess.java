/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.utils;

import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.ecKeyInteracts.OstCrypto;
import com.ost.mobilesdk.ecKeyInteracts.impls.OstAndroidSecureStorage;
import com.ost.mobilesdk.ecKeyInteracts.impls.OstSdkCrypto;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;

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
            ECKeyPair ecKeyPair = ostSdkOstCrypto.genECKey();
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