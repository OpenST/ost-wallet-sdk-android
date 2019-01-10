package com.ost.ostsdk.security;

import org.web3j.crypto.ECKeyPair;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface Crypto {
    byte[] genSCryptKey(byte[] feed, byte[] salt);

    byte[] genHKDFKey(byte[] feed, byte[] salt);

    byte[] genDigest(byte[] feed);

    ECKeyPair genECKey(String seed) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;

    byte[] aesEncryption(byte[] key, byte[] textToEncrypt, byte[] associatedData) throws Exception;

    byte[] aesDecryption(byte[] key, byte[] textToEncrypt, byte[] associatedData) throws Exception;
}