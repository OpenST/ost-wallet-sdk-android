package com.ost.ostsdk.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EIP1077Test {
    private static final String MSG_HASH = "0xc11e96ba445075d92706097a17994b0cc0d991515a40323bf4c0b55cb0eff751";

    @Test
    public void testEIP1077TransactionConversion() throws JSONException {
        JSONObject transactionHash = new JSONObject();
        transactionHash.put(EIP1077.TXN_VALUE, "1");
        transactionHash.put(EIP1077.TXN_FROM, "0x5a85a1E5a749A76dDf378eC2A0a2Ac310ca86Ba8");
        transactionHash.put(EIP1077.TXN_TO, "0xF281e85a0B992efA5fda4f52b35685dC5Ee67BEa");
        transactionHash.put(EIP1077.TXN_GAS_PRICE, "0");
        transactionHash.put(EIP1077.TXN_GAS, "0");
        transactionHash.put(EIP1077.TXN_CALL_PREFIX, "0x0");
        transactionHash.put(EIP1077.TXN_DATA, "0xF281e85a0B992efA5fda4f52b35685dC5Ee67BEa");
        transactionHash.put(EIP1077.TXN_NONCE, 1);

        try {
            String eip1077Hash = new EIP1077(transactionHash).toEIP1077TransactionHash();
            assertEquals(MSG_HASH, eip1077Hash);
            Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(eip1077Hash), ECKeyPair.create(Numeric.hexStringToByteArray("0xF281e85a0B992efA5fda4f52b35685dC5Ee67BEa")), false);
            BigInteger publicKey = Sign.signedPrefixedMessageToKey(Numeric.hexStringToByteArray(eip1077Hash), signatureData);
            assertEquals(27, signatureData.getV());
            assertEquals("0x405ceacacd719c669a34287674825b04f11aa2f7514ff39928eecd6c3c3fb52b", Numeric.toHexString(signatureData.getR()));
            assertEquals("0x0e6419a6a7f61f69d42ffb496df36697f2ef130a7f54cabe7d0f86cb7b75647b", Numeric.toHexString(signatureData.getS()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}