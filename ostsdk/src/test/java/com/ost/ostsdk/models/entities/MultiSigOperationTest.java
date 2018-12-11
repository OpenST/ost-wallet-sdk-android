package com.ost.ostsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class MultiSigOperationTest {

    @Test
    public void testMultiSigOperationInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any MultiSig  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        testMultiSigOperationJsonException(jsonObject);

        //Test Id with partial MultiSig attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(MultiSigOperation.ENCODED_DATA, "encodedData");
        jsonObject.put(MultiSigOperation.RAW_DATA, new JSONObject());
        jsonObject.put(MultiSigOperation.KIND, "kind");
        testMultiSigOperationJsonException(jsonObject);
    }

    @Test
    public void testMultiSigOperationValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any ExecutableRule  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(MultiSigOperation.ENCODED_DATA, "0x2901239");
        jsonObject.put(MultiSigOperation.KIND, "kind");
        jsonObject.put(MultiSigOperation.STATUS, "status");
        jsonObject.put(MultiSigOperation.TOKEN_HOLDER_ADDRESS, "0x9923232");

        JSONObject rawData = new JSONObject();
        jsonObject.put(MultiSigOperation.RAW_DATA, rawData);

        JSONObject signatures = new JSONObject();
        signatures.put("0x123", "0x456");
        jsonObject.put(MultiSigOperation.SIGNATURES, signatures);

        jsonObject.put(MultiSigOperation.USER_ID, "123");

        testMultiSigOperationJsonWithNoException(jsonObject);

        //Test Id with partial ExecutableRule attribute
        MultiSigOperation multiSigOperation = new MultiSigOperation(jsonObject);
        assertEquals("0x2901239", multiSigOperation.getEncodedData());
        assertEquals("kind", multiSigOperation.getKind());
        assertEquals("status", multiSigOperation.getStatus());
        assertEquals("0x9923232", multiSigOperation.getTokenHolderAddress());

        assertEquals(rawData, multiSigOperation.getRawData());
        assertEquals(signatures, multiSigOperation.getSignatures());

        assertEquals("123", multiSigOperation.getUserId());
        assertEquals("ID", multiSigOperation.getId());

    }


    private void testMultiSigOperationJsonException(JSONObject jsonObject) {
        try {
            MultiSigOperation multiSigOperation = new MultiSigOperation(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testMultiSigOperationJsonWithNoException(JSONObject jsonObject) {
        try {
            MultiSigOperation multiSigOperation = new MultiSigOperation(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}