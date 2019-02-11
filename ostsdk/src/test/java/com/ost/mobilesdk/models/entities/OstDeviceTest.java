package com.ost.mobilesdk.models.entities;

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
        jsonObject.put(OstDevice.ADDRESS, "ID");
        testMultiSigWalletJsonException(jsonObject);

        //Test Id with partial OstDevice attribute
        jsonObject = new JSONObject();
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
        jsonObject.put(OstDevice.USER_ID, "1234");
        jsonObject.put(OstDevice.DEVICE_NAME, "Device");
        jsonObject.put(OstDevice.DEVICE_UUID, "221");
        jsonObject.put(OstDevice.PERSONAL_SIGN_ADDRESS, "0x12345567");
        jsonObject.put(OstDevice.ADDRESS, "0x2901239");
        jsonObject.put(OstDevice.DEVICE_MANAGER_ADDRESS, "123");
        jsonObject.put(OstDevice.STATUS, "status");


        OstDevice ostDevice = new OstDevice(jsonObject);
        assertEquals("0x2901239", ostDevice.getAddress());
        assertEquals("status", ostDevice.getStatus());
        assertEquals("123", ostDevice.getDeviceManagerAddress());
        assertEquals("0x2901239", ostDevice.getId());
        assertEquals("1234", ostDevice.getUserId());
        assertEquals("221", ostDevice.getDeviceUuid());
        assertEquals("Device", ostDevice.getDeviceName());
        assertEquals("0x12345567", ostDevice.getPersonalSignAddress());

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