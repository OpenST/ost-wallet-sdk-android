package com.ost.ostsdk.security.impls;

import com.ost.ostsdk.security.Crypto;

import org.spongycastle.crypto.digests.KeccakDigest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.HKDFBytesGenerator;
import org.spongycastle.crypto.generators.SCrypt;
import org.spongycastle.crypto.params.HKDFParameters;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

public class OstSdkCrypto implements Crypto {
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
    public ECKeyPair genECKey(String seed) {
        return Bip32ECKeyPair.generateKeyPair(Numeric.hexStringToByteArray(seed));
    }
}
