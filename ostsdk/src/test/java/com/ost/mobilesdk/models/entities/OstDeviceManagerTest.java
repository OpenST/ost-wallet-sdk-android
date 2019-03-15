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

public class OstDeviceManagerTest {

    @Test
    public void testMultiSigInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstDeviceManager  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstDeviceManager.ADDRESS, "ID");
        testMultiSigJsonException(jsonObject);

        //Test Id with partial OstDeviceManager attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstDeviceManager.ADDRESS, "0x2901239");
        jsonObject.put(OstDeviceManager.REQUIREMENT, 1);
        testMultiSigJsonException(jsonObject);
    }

    @Test
    public void testMultiSigValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstTransaction  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstDeviceManager.ADDRESS, "0x2901239");
        jsonObject.put(OstDeviceManager.REQUIREMENT, 1);
        jsonObject.put(OstDeviceManager.USER_ID, "123");
        jsonObject.put(OstDeviceManager.NONCE, "1");
        testMultiSigJsonWithNoException(jsonObject);

        //Test Id with partial OstTransaction attribute
        OstDeviceManager ostDeviceManager = new OstDeviceManager(jsonObject);
        assertEquals("0x2901239", ostDeviceManager.getAddress());
        assertEquals(1, ostDeviceManager.getRequirement());
        assertEquals("123", ostDeviceManager.getUserId());
        assertEquals("0x2901239", ostDeviceManager.getId());
        assertEquals("1", ostDeviceManager.getNonce());
    }


    private void testMultiSigJsonException(JSONObject jsonObject) {
        try {
            OstDeviceManager ostDeviceManager = new OstDeviceManager(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testMultiSigJsonWithNoException(JSONObject jsonObject) {
        try {
            OstDeviceManager ostDeviceManager = new OstDeviceManager(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}