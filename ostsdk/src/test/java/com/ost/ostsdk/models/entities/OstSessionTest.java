package com.ost.ostsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OstSessionTest {

    @Test
    public void testTokenHolderSessionInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstSession  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        testTokenHolderSessionJsonException(jsonObject);

        //Test Id with partial OstSession attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstSession.ADDRESS, "0x232");
        jsonObject.put(OstSession.STATUS, "status");
        testTokenHolderSessionJsonException(jsonObject);
    }

    @Test
    public void testTokenHolderSessionValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstSession  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstSession.ADDRESS, "0x2901239");
        jsonObject.put(OstSession.STATUS, "status");
        jsonObject.put(OstSession.TOKEN_HOLDER_ID, "123");
        jsonObject.put(OstSession.BLOCK_HEIGHT, "1000");
        jsonObject.put(OstSession.EXPIRY_TIME, "100");
        jsonObject.put(OstSession.SPENDING_LIMIT, "9999");
        jsonObject.put(OstSession.REDEMPTION_LIMIT, "1000");
        jsonObject.put(OstSession.NONCE, "1");


        OstSession ostSession = new OstSession(jsonObject);
        assertEquals("0x2901239", ostSession.getAddress());
        assertEquals("status", ostSession.getStatus());
        assertEquals("123", ostSession.getTokenHolderId());
        assertEquals("1000", ostSession.getBlockHeight());
        assertEquals("100", ostSession.getExpiryTime());
        assertEquals("9999", ostSession.getSpendingLimit());
        assertEquals("1000", ostSession.getRedemptionLimit());
        assertEquals("ID", ostSession.getId());
        assertEquals("1", ostSession.getNonce());

    }


    private void testTokenHolderSessionJsonException(JSONObject jsonObject) {
        try {
            OstSession ostSession = new OstSession(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testTokenHolderSessionJsonWithNoException(JSONObject jsonObject) {
        try {
            OstSession ostSession = new OstSession(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}