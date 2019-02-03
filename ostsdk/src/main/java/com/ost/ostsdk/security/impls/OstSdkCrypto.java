package com.ost.ostsdk.security.impls;

import com.ost.ostsdk.security.OstCrypto;

import org.spongycastle.crypto.digests.KeccakDigest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.HKDFBytesGenerator;
import org.spongycastle.crypto.generators.SCrypt;
import org.spongycastle.crypto.params.HKDFParameters;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static org.web3j.crypto.Hash.sha256;

public class OstSdkCrypto implements OstCrypto {

    private static final String ALGORITHM = "AES";
    private static final String ALGORITHM_NAME = "AES/GCM/NoPadding";

    @Override
    public byte[] genSCryptKey(byte[] feed, byte[] salt) {
        return SCrypt.generate(feed, salt, 2, 2, 2, 32);
    }

    @Override
    public byte[] genHKDFKey(byte[] feed, byte[] salt) {
        HKDFBytesGenerator hkdfBytesGenerator = new HKDFBytesGenerator(new SHA256Digest());
        hkdfBytesGenerator.init(new HKDFParameters(feed, salt, null));
        byte[] hkdfOutput = new byte[32];
        hkdfBytesGenerator.generateBytes(hkdfOutput, 0, 32);
        return hkdfOutput;
    }

    @Override
    public byte[] genDigest(byte[] feed) {
        KeccakDigest keccakDigest = new KeccakDigest(256);
        keccakDigest.update(feed, 0, 32);
        byte[] hash = new byte[32];
        keccakDigest.doFinal(hash, 0);
        return hash;
    }

    @Override
    public String genMnemonics(String passPhrase) {
        byte[] initialEntropy = new byte[16];
        SecureRandom secureRandom = new SecureRandom(passPhrase.getBytes());
        secureRandom.nextBytes(initialEntropy);

        return MnemonicUtils.generateMnemonic(initialEntropy);
    }

    @Override
    public ECKeyPair genECKey(String passPhrase) {
        String mnemonics = genMnemonics(passPhrase);
        return  genECKeyFromMnemonics(mnemonics, passPhrase);
    }

    @Override
    public ECKeyPair genECKeyFromMnemonics(String mnemonics, String passPhrase) {
        byte[] mnemonicSeed = MnemonicUtils.generateSeed(mnemonics, passPhrase);
        return ECKeyPair.create(sha256(mnemonicSeed));
    }

    @Override
    public byte[] aesEncryption(byte[] key, byte[] textToEncrypt, byte[] associatedData) throws Exception {
        return aesGcm(key, textToEncrypt, associatedData, Cipher.ENCRYPT_MODE);
    }

    @Override
    public byte[] aesDecryption(byte[] key, byte[] textToEncrypt, byte[] associatedData) throws Exception {
        return aesGcm(key, textToEncrypt, associatedData, Cipher.DECRYPT_MODE);
    }

    private byte[] aesGcm(byte[] key, byte[] encrypted, byte[] associatedData, final int mode) throws Exception {
        byte[] data = new byte[32];

        Cipher cipher;
        cipher = Cipher.getInstance(ALGORITHM_NAME);
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
        final IvParameterSpec ivSpec = new IvParameterSpec("iv".getBytes());

        cipher.init(mode, secretKey, ivSpec);
        cipher.updateAAD(associatedData);
        data = cipher.doFinal(encrypted);

        return data;
    }

    public static OstCrypto getInstance() {
        return new OstSdkCrypto();
    }

}
