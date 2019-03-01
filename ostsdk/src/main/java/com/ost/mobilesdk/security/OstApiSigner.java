package com.ost.mobilesdk.security;

import com.ost.mobilesdk.network.OstHttpRequestClient;

import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

public class OstApiSigner implements OstHttpRequestClient.ApiSigner {

    String mUserID;
    public OstApiSigner(String userId) {
        mUserID = userId;
    }

    /**
     * Sign Api using Api key
     * @param dataToSign
     * @return
     */
    @Override
    public String sign(byte[] dataToSign) {
        InternalKeyManager ikm = new InternalKeyManager(mUserID);
        Sign.SignatureData signatureData = ikm.signBytesWithApiSigner(dataToSign);
        ikm = null;
        return OstApiSigner.createStringSignature(signatureData);
    }

    private static String createStringSignature(Sign.SignatureData signatureData) {
        return Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + String.format("%02x",(signatureData.getV()));
    }
}