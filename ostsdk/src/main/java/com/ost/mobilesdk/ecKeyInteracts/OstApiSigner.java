package com.ost.mobilesdk.ecKeyInteracts;

import com.ost.mobilesdk.network.OstHttpRequestClient;

import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

public class OstApiSigner implements OstHttpRequestClient.ApiSigner {

    String mUserID;
    public OstApiSigner(String userId) {
        mUserID = userId;
    }

    /**
     * Generates signature for HTTP Api calls (ETH Personal Sign).
     * @param dataToSign - byte[] to sign.
     * @return
     */
    @Override
    public String sign(byte[] dataToSign) {
        InternalKeyManager ikm = null;
        try {
            ikm = new InternalKeyManager(mUserID);
            return ikm.signBytesWithApiSigner(dataToSign);
        } finally {
            ikm = null;
        }
    }

    private static String createStringSignature(Sign.SignatureData signatureData) {
        return Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + String.format("%02x",(signatureData.getV()));
    }
}