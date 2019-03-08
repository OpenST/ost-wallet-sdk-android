package com.ost.mobilesdk.ecKeyInteracts;

import org.web3j.crypto.ECKeyPair;

public interface OstCrypto {
    byte[] genSCryptKey(byte[] feed, byte[] salt);

    byte[] genHKDFKey(byte[] feed, byte[] salt);

    byte[] genDigest(byte[] feed);

    byte[] genDigest(byte[] feed, int times);

    String genMnemonics();

    ECKeyPair genECKey();

    ECKeyPair genHDKey(byte[] seed);

    ECKeyPair genECKeyFromMnemonics(String mnemonics);

    byte[] aesEncryption(byte[] key, byte[] textToEncrypt, byte[] associatedData) throws Exception;

    byte[] aesDecryption(byte[] key, byte[] textToEncrypt, byte[] associatedData) throws Exception;
}