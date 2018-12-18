package com.ost.ostsdk.security;

import org.web3j.crypto.ECKeyPair;

public interface Crypto {
    byte[] genSCryptKey(byte[] feed, byte[] salt);

    byte[] genHKDFKey(byte[] feed, byte[] salt);

    byte[] genDigest(byte[] feed);

    ECKeyPair genECKey(String seed);

}