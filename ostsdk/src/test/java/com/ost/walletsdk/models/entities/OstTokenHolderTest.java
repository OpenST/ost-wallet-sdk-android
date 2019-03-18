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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OstTokenHolderTest {


    @Test
    public void testTokenHolderValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstTokenHolder  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstTokenHolder.ADDRESS, "0x2901239");
        jsonObject.put(OstTokenHolder.USER_ID, "123");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("0x12345678");


        OstTokenHolder ostTokenHolder = new OstTokenHolder(jsonObject);
        assertEquals("0x2901239", ostTokenHolder.getAddress());
        assertEquals("123", ostTokenHolder.getUserId());
        assertEquals("0x2901239", ostTokenHolder.getId());

    }


    private void testTokenHolderJsonException(JSONObject jsonObject) {
        try {
            OstTokenHolder ostTokenHolder = new OstTokenHolder(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testTokenHolderJsonWithNoException(JSONObject jsonObject) {
        try {
            OstTokenHolder ostTokenHolder = new OstTokenHolder(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}