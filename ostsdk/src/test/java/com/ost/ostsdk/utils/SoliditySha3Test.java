package com.ost.ostsdk.utils;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SoliditySha3Test {
    private static final String MSG_HASH = "0xc11e96ba445075d92706097a17994b0cc0d991515a40323bf4c0b55cb0eff751";

    @Test
    public void testSoliditySha3MultipleArguments() {

        String sha3Hash = null;

        try {
            sha3Hash = new SoliditySha3().soliditySha3(
                    new JSONObject("{ t: 'bytes', v: '0x19' }"),
                    new JSONObject("{ t: 'bytes', v: '0x00' }"),
                    new JSONObject("{ t: 'address', v: '0x5a85a1e5a749a76ddf378ec2a0a2ac310ca86ba8' }"),
                    new JSONObject("{ t: 'address', v: '0xf281e85a0b992efa5fda4f52b35685dc5ee67bea' }"),
                    new JSONObject("{ t: 'uint8', v: '1' }"),
                    new JSONObject("{ t: 'bytes', v:'0x70de87124c30996cba71d1ee38150c289d019c50d7cfeb71c6b3194e9cfda5c9' }"),
                    new JSONObject("{ t: 'uint256', v: '1' }"),
                    new JSONObject("{ t: 'uint8', v: '0' }"),
                    new JSONObject("{ t: 'uint8', v: '0' }"),
                    new JSONObject("{ t: 'uint8', v: '0' }"),
                    new JSONObject("{ t: 'bytes4', v: '0x' }"),
                    new JSONObject("{ t: 'uint8', v: '0' }"),
                    new JSONObject("{ t: 'bytes32', v: '0x00' }")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(MSG_HASH, sha3Hash);
    }

    @Test
    public void testSoliditySha3StringArguments() {
        String sha3Hash;
        String message = "testString";
        try {
            sha3Hash = new SoliditySha3().soliditySha3(message);
            assertEquals("0xff5fa2a47214bd12e073aa16f8c5f68cbcae28ed9131ae8b413805ba2e1aa7d3", sha3Hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSoliditySha3SingleArguments() {
        String sha3Hash;
        try {
            sha3Hash = new SoliditySha3().soliditySha3("1");
            assertEquals("0x637df1eb4c09a98dc453cdea36c6242657b34c2792a700647d14e62033140a83", sha3Hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}