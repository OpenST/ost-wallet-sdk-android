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

public class OstRuleTest {

    @Test
    public void testRuleInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstRule  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstRule.ID, "ID");
        testRuleJsonException(jsonObject);

        //Test Id with partial OstRule attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstRule.ID, "ID");
        jsonObject.put(OstRule.ADDRESS, "0x232");
        jsonObject.put(OstRule.ABI, "abi");
        testRuleJsonException(jsonObject);
    }

    @Test
    public void testRuleValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstRule  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstRule.ID, "ID");
        jsonObject.put(OstRule.ADDRESS, "0x2901239");
        jsonObject.put(OstRule.NAME, "name");
        jsonObject.put(OstRule.ABI, "abi");
        jsonObject.put(OstRule.TOKEN_ID, "123");
        jsonObject.put(OstRule.CALL_PREFIX, "0x0");

        OstRule ostRule = new OstRule(jsonObject);
        assertEquals("0x2901239", ostRule.getAddress());
        assertEquals("name", ostRule.getName());
        assertEquals("abi", ostRule.getAbi());
        assertEquals("123", ostRule.getTokenId());
        assertEquals("ID", ostRule.getId());

    }


    private void testRuleJsonException(JSONObject jsonObject) {
        try {
            OstRule ostRule = new OstRule(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testRuleJsonWithNoException(JSONObject jsonObject) {
        try {
            OstRule ostRule = new OstRule(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}