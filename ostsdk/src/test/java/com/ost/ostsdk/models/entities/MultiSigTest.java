package com.ost.ostsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MultiSigTest {

    @Test
    public void testMultiSigInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any MultiSig  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        testMultiSigJsonException(jsonObject);

        //Test Id with partial MultiSig attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(MultiSig.ADDRESS, "0x2901239");
        jsonObject.put(MultiSig.TOKEN_HOLDER_ID, "123");
        jsonObject.put(MultiSig.REQUIREMENT, 1);
        testMultiSigJsonException(jsonObject);
    }

    @Test
    public void testMultiSigValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any ExecutableRule  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(MultiSig.ADDRESS, "0x2901239");
        jsonObject.put(MultiSig.TOKEN_HOLDER_ID, "123");
        jsonObject.put(MultiSig.REQUIREMENT, 1);
        jsonObject.put(MultiSig.AUTHORIZE_SESSION_CALL_PREFIX, "callPrefix");
        jsonObject.put(MultiSig.USER_ID, "123");
        testMultiSigJsonWithNoException(jsonObject);

        //Test Id with partial ExecutableRule attribute
        MultiSig multiSig = new MultiSig(jsonObject);
        assertEquals("0x2901239", multiSig.getAddress());
        assertEquals("123", multiSig.getTokenHolderId());
        assertEquals(1, multiSig.getRequirement());
        assertEquals("callPrefix", multiSig.getAuthorizeSessionCallPrefix());
        assertEquals("123", multiSig.getUserId());
        assertEquals("ID", multiSig.getId());

    }


    private void testMultiSigJsonException(JSONObject jsonObject) {
        try {
            MultiSig multiSig = new MultiSig(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testMultiSigJsonWithNoException(JSONObject jsonObject) {
        try {
            MultiSig multiSig = new MultiSig(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}