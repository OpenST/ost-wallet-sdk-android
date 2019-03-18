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

public class OstDeviceManagerOperationTest {

    @Test
    public void testMultiSigOperationInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstDeviceManager  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstDeviceManagerOperation.ID, "ID");
        testMultiSigOperationJsonException(jsonObject);

        //Test Id with partial OstDeviceManager attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstDeviceManagerOperation.ID, "ID");
        jsonObject.put(OstDeviceManagerOperation.KIND, "encodedData");
        jsonObject.put(OstDeviceManagerOperation.OPERATION, new JSONObject());
        jsonObject.put(OstDeviceManagerOperation.DEVICE_MANAGER_ADDRESS, "kind");
        testMultiSigOperationJsonException(jsonObject);
    }

    @Test
    public void testMultiSigOperationValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any DeviceOperation  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstDeviceManagerOperation.ID, "ID");
        jsonObject.put(OstDeviceManagerOperation.DEVICE_MANAGER_ADDRESS, "0x2901239");
        jsonObject.put(OstDeviceManagerOperation.KIND, "kind");
        jsonObject.put(OstDeviceManagerOperation.DEVICE_MANAGER_ID, "0x9923232");

        JSONObject rawData = new JSONObject();
        jsonObject.put(OstDeviceManagerOperation.OPERATION, rawData);

        JSONObject signatures = new JSONObject();
        signatures.put("0x123", "0x456");
        jsonObject.put(OstDeviceManagerOperation.SIGNATURES, signatures);

        jsonObject.put(OstDeviceManagerOperation.SAFE_TXN_GAS, "0x123");
        jsonObject.put(OstDeviceManagerOperation.USER_ID, "123");
        jsonObject.put(OstDeviceManagerOperation.CALL_DATA, "123");
        jsonObject.put(OstDeviceManagerOperation.RAW_CALL_DATA, "123");

        testMultiSigOperationJsonWithNoException(jsonObject);

        //Test Id with partial DeviceOperation attribute
        OstDeviceManagerOperation ostDeviceManagerOperation = new OstDeviceManagerOperation(jsonObject);
        assertEquals("0x2901239", ostDeviceManagerOperation.getDeviceManagerAddress());
        assertEquals("kind", ostDeviceManagerOperation.getKind());
        assertEquals("0x9923232", ostDeviceManagerOperation.getDeviceManagerId());

        assertEquals("0x123", ostDeviceManagerOperation.getSafeTxnGas());
        assertEquals(signatures, ostDeviceManagerOperation.getSignatures());

        assertEquals("123", ostDeviceManagerOperation.getUserId());
        assertEquals("ID", ostDeviceManagerOperation.getId());

    }


    private void testMultiSigOperationJsonException(JSONObject jsonObject) {
        try {
            OstDeviceManagerOperation ostDeviceManagerOperation = new OstDeviceManagerOperation(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testMultiSigOperationJsonWithNoException(JSONObject jsonObject) {
        try {
            OstDeviceManagerOperation ostDeviceManagerOperation = new OstDeviceManagerOperation(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}