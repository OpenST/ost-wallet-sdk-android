package com.ost.ostsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TokenHolderSessionTest {

    @Test
    public void testTokenHolderSessionInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any TokenHolderSession  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        testTokenHolderSessionJsonException(jsonObject);

        //Test Id with partial TokenHolderSession attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(TokenHolderSession.ADDRESS, "0x232");
        jsonObject.put(TokenHolderSession.STATUS, "status");
        testTokenHolderSessionJsonException(jsonObject);
    }

    @Test
    public void testTokenHolderSessionValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any TokenHolderSession  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(TokenHolderSession.ADDRESS, "0x2901239");
        jsonObject.put(TokenHolderSession.STATUS, "status");
        jsonObject.put(TokenHolderSession.TOKEN_HOLDER_ID, "123");
        jsonObject.put(TokenHolderSession.BLOCK_HEIGHT, "1000");
        jsonObject.put(TokenHolderSession.EXPIRY_TIME, "100");
        jsonObject.put(TokenHolderSession.SPENDING_LIMIT, "9999");
        jsonObject.put(TokenHolderSession.REDEMPTION_LIMIT, "1000");
        jsonObject.put(TokenHolderSession.NONCE, "1");


        TokenHolderSession tokenHolderSession = new TokenHolderSession(jsonObject);
        assertEquals("0x2901239", tokenHolderSession.getAddress());
        assertEquals("status", tokenHolderSession.getStatus());
        assertEquals("123", tokenHolderSession.getTokenHolderId());
        assertEquals("1000", tokenHolderSession.getBlockHeight());
        assertEquals("100", tokenHolderSession.getExpiryTime());
        assertEquals("9999", tokenHolderSession.getSpendingLimit());
        assertEquals("1000", tokenHolderSession.getRedemptionLimit());
        assertEquals("ID", tokenHolderSession.getId());
        assertEquals("1", tokenHolderSession.getNonce());

    }


    private void testTokenHolderSessionJsonException(JSONObject jsonObject) {
        try {
            TokenHolderSession tokenHolderSession = new TokenHolderSession(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testTokenHolderSessionJsonWithNoException(JSONObject jsonObject) {
        try {
            TokenHolderSession tokenHolderSession = new TokenHolderSession(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}