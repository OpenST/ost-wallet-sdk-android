package com.ost.ostsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OstTokenHolderSessionTest {

    @Test
    public void testTokenHolderSessionInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstTokenHolderSession  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        testTokenHolderSessionJsonException(jsonObject);

        //Test Id with partial OstTokenHolderSession attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstTokenHolderSession.ADDRESS, "0x232");
        jsonObject.put(OstTokenHolderSession.STATUS, "status");
        testTokenHolderSessionJsonException(jsonObject);
    }

    @Test
    public void testTokenHolderSessionValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstTokenHolderSession  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstTokenHolderSession.ADDRESS, "0x2901239");
        jsonObject.put(OstTokenHolderSession.STATUS, "status");
        jsonObject.put(OstTokenHolderSession.TOKEN_HOLDER_ID, "123");
        jsonObject.put(OstTokenHolderSession.BLOCK_HEIGHT, "1000");
        jsonObject.put(OstTokenHolderSession.EXPIRY_TIME, "100");
        jsonObject.put(OstTokenHolderSession.SPENDING_LIMIT, "9999");
        jsonObject.put(OstTokenHolderSession.REDEMPTION_LIMIT, "1000");
        jsonObject.put(OstTokenHolderSession.NONCE, "1");


        OstTokenHolderSession ostTokenHolderSession = new OstTokenHolderSession(jsonObject);
        assertEquals("0x2901239", ostTokenHolderSession.getAddress());
        assertEquals("status", ostTokenHolderSession.getStatus());
        assertEquals("123", ostTokenHolderSession.getTokenHolderId());
        assertEquals("1000", ostTokenHolderSession.getBlockHeight());
        assertEquals("100", ostTokenHolderSession.getExpiryTime());
        assertEquals("9999", ostTokenHolderSession.getSpendingLimit());
        assertEquals("1000", ostTokenHolderSession.getRedemptionLimit());
        assertEquals("ID", ostTokenHolderSession.getId());
        assertEquals("1", ostTokenHolderSession.getNonce());

    }


    private void testTokenHolderSessionJsonException(JSONObject jsonObject) {
        try {
            OstTokenHolderSession ostTokenHolderSession = new OstTokenHolderSession(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testTokenHolderSessionJsonWithNoException(JSONObject jsonObject) {
        try {
            OstTokenHolderSession ostTokenHolderSession = new OstTokenHolderSession(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}