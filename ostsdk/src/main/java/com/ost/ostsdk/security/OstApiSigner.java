package com.ost.ostsdk.security;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

public class OstApiSigner {
    private final ECKeyPair mECKeyPair;

    public OstApiSigner(byte[] key) {
        mECKeyPair = ECKeyPair.create(key);
    }

    /**
     * Sign Api using Api key
     * @param dataToSign
     * @return
     */
    public String sign(byte[] dataToSign) {
        return createStringSignature(Sign.signPrefixedMessage(dataToSign, mECKeyPair));
    }

    public String getAddress() {
        return Credentials.create(mECKeyPair).getAddress();
    }

    private String createStringSignature(Sign.SignatureData signatureData) {
        return Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + String.format("%02x",(signatureData.getV()));
    }
}