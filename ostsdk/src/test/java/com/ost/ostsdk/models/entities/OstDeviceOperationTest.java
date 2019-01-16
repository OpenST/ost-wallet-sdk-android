package com.ost.ostsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OstDeviceOperationTest {

    @Test
    public void testMultiSigOperationInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstDeviceManager  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        testMultiSigOperationJsonException(jsonObject);

        //Test Id with partial OstDeviceManager attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstDeviceOperation.ENCODED_DATA, "encodedData");
        jsonObject.put(OstDeviceOperation.RAW_DATA, new JSONObject());
        jsonObject.put(OstDeviceOperation.KIND, "kind");
        testMultiSigOperationJsonException(jsonObject);
    }

    @Test
    public void testMultiSigOperationValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstExecutableRule  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstDeviceOperation.ENCODED_DATA, "0x2901239");
        jsonObject.put(OstDeviceOperation.KIND, "kind");
        jsonObject.put(OstDeviceOperation.STATUS, "status");
        jsonObject.put(OstDeviceOperation.TOKEN_HOLDER_ADDRESS, "0x9923232");

        JSONObject rawData = new JSONObject();
        jsonObject.put(OstDeviceOperation.RAW_DATA, rawData);

        JSONObject signatures = new JSONObject();
        signatures.put("0x123", "0x456");
        jsonObject.put(OstDeviceOperation.SIGNATURES, signatures);

        jsonObject.put(OstDeviceOperation.USER_ID, "123");

        testMultiSigOperationJsonWithNoException(jsonObject);

        //Test Id with partial OstExecutableRule attribute
        OstDeviceOperation ostDeviceOperation = new OstDeviceOperation(jsonObject);
        assertEquals("0x2901239", ostDeviceOperation.getEncodedData());
        assertEquals("kind", ostDeviceOperation.getKind());
        assertEquals("status", ostDeviceOperation.getStatus());
        assertEquals("0x9923232", ostDeviceOperation.getTokenHolderAddress());

        assertEquals(rawData, ostDeviceOperation.getRawData());
        assertEquals(signatures, ostDeviceOperation.getSignatures());

        assertEquals("123", ostDeviceOperation.getUserId());
        assertEquals("ID", ostDeviceOperation.getId());

    }


    private void testMultiSigOperationJsonException(JSONObject jsonObject) {
        try {
            OstDeviceOperation ostDeviceOperation = new OstDeviceOperation(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testMultiSigOperationJsonWithNoException(JSONObject jsonObject) {
        try {
            OstDeviceOperation ostDeviceOperation = new OstDeviceOperation(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}