/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OstUserTest {

    @Test
    public void testUserInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstUser  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstUser.ID, "ID");
        testUserJsonException(jsonObject);

        //Test Id with partial OstUser attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstUser.ID, "ID");
        jsonObject.put(OstUser.TOKEN_ID, "123");
        jsonObject.put(OstUser.TYPE, OstUser.TYPE_VALUE.USER);
        testUserJsonException(jsonObject);
    }

    @Test
    public void testUserValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstUser  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstUser.ID, "ID");
        jsonObject.put(OstUser.TOKEN_ID, "0x2901239");
        jsonObject.put(OstUser.TOKEN_HOLDER_ADDRESS, "123");
        jsonObject.put(OstUser.DEVICE_MANAGER_ADDRESS, "1");
        jsonObject.put(OstUser.TYPE, OstUser.TYPE_VALUE.USER);

        OstUser ostUser =  new OstUser(jsonObject);
        assertEquals("0x2901239", ostUser.getTokenId());
        assertEquals("123", ostUser.getTokenHolderAddress());
        assertEquals("ID", ostUser.getId());
        assertEquals("1", ostUser.getDeviceManagerAddress());
        assertEquals(OstUser.TYPE_VALUE.USER, ostUser.getType());
    }


    private void testUserJsonException(JSONObject jsonObject) {
        try {
            OstUser ostUser = OstUser.parse(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testUserJsonWithNoException(JSONObject jsonObject) {
        try {
            OstUser ostUser = OstUser.parse(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}