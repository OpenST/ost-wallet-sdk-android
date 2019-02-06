package com.ost.mobilesdk.security;

import org.web3j.crypto.ECKeyPair;

public interface OstCrypto {
    byte[] genSCryptKey(byte[] feed, byte[] salt);

    byte[] genHKDFKey(byte[] feed, byte[] salt);

    byte[] genDigest(byte[] feed);

    byte[] genDigest(byte[] feed, int times);

    String genMnemonics(String passPhrase);

    ECKeyPair genECKey(String passPhrase);

    ECKeyPair genECKeyFromMnemonics(String mnemonics, String passPhrase);

    byte[] aesEncryption(byte[] key, byte[] textToEncrypt, byte[] associatedData) throws Exception;

    byte[] aesDecryption(byte[] key, byte[] textToEncrypt, byte[] associatedData) throws Exception;
}