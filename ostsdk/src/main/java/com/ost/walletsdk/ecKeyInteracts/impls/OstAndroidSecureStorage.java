/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ecKeyInteracts.impls;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.ecKeyInteracts.OstSecureStorage;
import com.ost.walletsdk.workflows.errors.OstError;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;


public class OstAndroidSecureStorage implements OstSecureStorage {

    private static final String TAG = OstAndroidSecureStorage.class.getName();
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String TRANSFORMATION_ASYMMETRIC = "RSA/ECB/PKCS1Padding";
    private static final String RSA = "RSA";
    private final Context mContext;
    private String mKeyAlias;

    private KeyStore mKeyStore;

    private OstAndroidSecureStorage(@NonNull Context context, @NonNull String keyAlias) {
        this.mContext = context.getApplicationContext();
        this.mKeyAlias = keyAlias;
        try {
            mKeyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            mKeyStore.load(null);

            if (null == getKey()) {
                generateKey();
            }
        } catch (Exception ex) {
            Log.e(TAG, "Exception faced while build object" + ex.getMessage(), ex.getCause());
            throw OstError.SdkError("oass_constructor_1", ex);
        }
    }

    public static OstSecureStorage getInstance(@NonNull Context context, @NonNull String keyAlias) {
        return new OstAndroidSecureStorage(context, keyAlias);
    }

    @Override
    public byte[] encrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION_ASYMMETRIC);
            cipher.init(Cipher.ENCRYPT_MODE, Objects.requireNonNull(getKey()).getPublic());
            return cipher.doFinal(data);
        } catch (Exception ex) {
            Log.e(TAG, "Exception faced while encryption " + ex.getMessage(), ex.getCause());
        }
        return null;
    }

    @Override
    public byte[] decrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION_ASYMMETRIC);
            cipher.init(Cipher.DECRYPT_MODE, Objects.requireNonNull(getKey()).getPrivate());
            return cipher.doFinal(data);
        } catch (Throwable th) {
            OstError ostError = OstError.SdkError("oass_d_1", th);
            Log.e(TAG, "Exception faced while decryption " + th.getMessage(), th.getCause());
            throw ostError;
        }
    }

    private void generateKey() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA, ANDROID_KEY_STORE);
        AlgorithmParameterSpec algorithmParameterSpec;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            algorithmParameterSpec = initGeneratorWithKeyPairGeneratorSpec();
        } else {
            algorithmParameterSpec = initGeneratorWithKeyGenParameterSpec();
        }
        keyPairGenerator.initialize(algorithmParameterSpec);
        keyPairGenerator.genKeyPair();

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private KeyGenParameterSpec initGeneratorWithKeyGenParameterSpec() {
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(mKeyAlias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setUserAuthenticationRequired(false);

        return builder.build();
    }

    private KeyPairGeneratorSpec initGeneratorWithKeyPairGeneratorSpec() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 20);

        KeyPairGeneratorSpec.Builder builder = new KeyPairGeneratorSpec.Builder(mContext)
                .setAlias(mKeyAlias)
                .setSerialNumber(BigInteger.ONE)
                .setSubject(new X500Principal("CN=$mKeyAlias CA Certificate"))
                .setStartDate(startDate.getTime())
                .setEndDate(endDate.getTime());

        return builder.build();
    }

    private KeyPair getKey() {
        PrivateKey privateKey = null;
        Certificate keyStoreCertificate  = null;
        PublicKey publicKey = null;
        try {
            privateKey = (PrivateKey) mKeyStore.getKey(mKeyAlias, null);
            keyStoreCertificate = mKeyStore.getCertificate(mKeyAlias);
            publicKey = (null == keyStoreCertificate ? null : keyStoreCertificate.getPublicKey());
            if (null == privateKey || null == keyStoreCertificate || null == publicKey) {
                throw new Error("Failed to get private key from key store");
            }
            return new KeyPair(publicKey, privateKey);
        } catch (Throwable th) {
            OstError ostError = OstError.SdkError("oass_kp_gk_1", th);
            ostError.addErrorInfo("oass_kp_gk_1.key_alias", mKeyAlias);

            String isPrivateKeyNull = null ==  privateKey? "true": "false";
            ostError.addErrorInfo("oass_kp_gk_1.isPrivateKeyNull", isPrivateKeyNull);

            String isPublicKeyNull = null ==  publicKey? "true": "false";
            ostError.addErrorInfo("oass_kp_gk_1.isPublicKeyNull", isPublicKeyNull);

            String isKeyStoreCertificateNull = null ==  keyStoreCertificate? "true": "false";
            ostError.addErrorInfo("oass_kp_gk_1.isKeyStoreCertificateNull", isKeyStoreCertificateNull);

            Log.d(TAG, "Exception faced in getId ");
            throw ostError;
        }
    }
}