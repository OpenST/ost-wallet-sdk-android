package com.ost.ostsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MultiSigWalletTest {

    @Test
    public void testMultiSigWalletInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any MultiSigWallet  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        testMultiSigWalletJsonException(jsonObject);

        //Test Id with partial MultiSigWallet attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(MultiSigWallet.ADDRESS, "0x232");
        jsonObject.put(MultiSigWallet.STATUS, "status");
        testMultiSigWalletJsonException(jsonObject);
    }

    @Test
    public void testMultiSigWalletValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any ExecutableRule  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(MultiSigWallet.ADDRESS, "0x2901239");
        jsonObject.put(MultiSigWallet.MULTI_SIG_ID, "123");
        jsonObject.put(MultiSigWallet.STATUS, "status");
        jsonObject.put(MultiSigWallet.NONCE, "1");

        MultiSigWallet multiSigWallet = new MultiSigWallet(jsonObject);
        assertEquals("0x2901239", multiSigWallet.getAddress());
        assertEquals("status", multiSigWallet.getStatus());
        assertEquals("123", multiSigWallet.getMultiSigId());
        assertEquals("ID", multiSigWallet.getId());
        assertEquals("1", multiSigWallet.getNonce());

    }


    private void testMultiSigWalletJsonException(JSONObject jsonObject) {
        try {
            MultiSigWallet multiSigWallet = new MultiSigWallet(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testMultiSigWalletJsonWithNoException(JSONObject jsonObject) {
        try {
            MultiSigWallet multiSigWallet = new MultiSigWallet(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}