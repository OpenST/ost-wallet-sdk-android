/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.models.entities;

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
        jsonObject.put(OstSession.ADDRESS, "ID");
        testTokenHolderSessionJsonException(jsonObject);

        //Test Id with partial OstSession attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstSession.ADDRESS, "ID");
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
        jsonObject.put(OstSession.ADDRESS, "0x2901239");
        jsonObject.put(OstSession.USER_ID, "1234");
        jsonObject.put(OstSession.STATUS, "status");
        jsonObject.put(OstSession.EXPIRATION_HEIGHT, "1000");
        jsonObject.put(OstSession.SPENDING_LIMIT, "9999");
        jsonObject.put(OstSession.NONCE, 1);


        OstSession ostSession = new OstSession(jsonObject);
        assertEquals("0x2901239", ostSession.getAddress());
        assertEquals("status", ostSession.getStatus());
        assertEquals("9999", ostSession.getSpendingLimit());
        assertEquals("0x2901239", ostSession.getId());
        assertEquals(1, ostSession.getNonce());

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