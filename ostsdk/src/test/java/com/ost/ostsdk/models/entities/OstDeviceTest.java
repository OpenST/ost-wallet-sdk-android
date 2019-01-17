package com.ost.ostsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OstDeviceTest {

    @Test
    public void testMultiSigWalletInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstDevice  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        testMultiSigWalletJsonException(jsonObject);

        //Test Id with partial OstDevice attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstDevice.ADDRESS, "0x232");
        jsonObject.put(OstDevice.STATUS, "status");
        testMultiSigWalletJsonException(jsonObject);
    }

    @Test
    public void testMultiSigWalletValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstDevice  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstDevice.ADDRESS, "0x2901239");
        jsonObject.put(OstDevice.MULTI_SIG_ID, "123");
        jsonObject.put(OstDevice.STATUS, "status");


        OstDevice ostDevice = new OstDevice(jsonObject);
        assertEquals("0x2901239", ostDevice.getAddress());
        assertEquals("status", ostDevice.getStatus());
        assertEquals("123", ostDevice.getMultiSigId());
        assertEquals("ID", ostDevice.getId());

    }


    private void testMultiSigWalletJsonException(JSONObject jsonObject) {
        try {
            OstDevice ostDevice = new OstDevice(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testMultiSigWalletJsonWithNoException(JSONObject jsonObject) {
        try {
            OstDevice ostDevice = new OstDevice(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}