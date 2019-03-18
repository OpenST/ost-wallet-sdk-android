/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ecKeyInteracts;

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