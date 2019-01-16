package com.ost.ostsdk.models.entities;

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
        jsonObject.put(OstBaseEntity.ID, "ID");
        testUserJsonException(jsonObject);

        //Test Id with partial OstUser attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstUser.TOKEN_ID, "123");
        jsonObject.put(OstUser.NAME, "status");
        testUserJsonException(jsonObject);
    }

    @Test
    public void testUserValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstUser  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstUser.TOKEN_ID, "0x2901239");
        jsonObject.put(OstUser.TOKEN_HOLDER_ID, "123");
        jsonObject.put(OstUser.NAME, "name");
        jsonObject.put(OstUser.MULTI_SIG_ID, "1");

        OstUser ostUser = new OstUser(jsonObject);
        assertEquals("0x2901239", ostUser.getTokenId());
        assertEquals("123", ostUser.getTokenHolderId());
        assertEquals("name", ostUser.getName());
        assertEquals("ID", ostUser.getId());
        assertEquals("1", ostUser.getMultiSigId());
    }


    private void testUserJsonException(JSONObject jsonObject) {
        try {
            OstUser ostUser = new OstUser(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testUserJsonWithNoException(JSONObject jsonObject) {
        try {
            OstUser ostUser = new OstUser(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}