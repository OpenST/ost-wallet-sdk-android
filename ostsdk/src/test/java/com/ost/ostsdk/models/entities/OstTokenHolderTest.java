package com.ost.ostsdk.models.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OstTokenHolderTest {

    @Test
    public void testTokenHolderInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstTokenHolder  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        testTokenHolderJsonException(jsonObject);

        //Test Id with partial OstTokenHolder attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstTokenHolder.ADDRESS, "0x232");
//        jsonObject.put(OstTokenHolder.REQUIREMENTS, 1);
        testTokenHolderJsonException(jsonObject);
    }

    @Test
    public void testTokenHolderValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstTokenHolder  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstTokenHolder.ADDRESS, "0x2901239");
//        jsonObject.put(OstTokenHolder.REQUIREMENTS, 1);
        jsonObject.put(OstTokenHolder.USER_ID, "123");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("0x12345678");
//        jsonObject.put(OstTokenHolder.EXECUTE_RULE_CALL_PREFIX, "callPrefix");


        OstTokenHolder ostTokenHolder = new OstTokenHolder(jsonObject);
        assertEquals("0x2901239", ostTokenHolder.getAddress());
//        assertEquals(1, ostTokenHolder.getRequirements());
        assertEquals("123", ostTokenHolder.getUserId());
//        assertEquals("callPrefix", ostTokenHolder.getExecuteRuleCallPrefix());
        assertEquals("ID", ostTokenHolder.getId());

    }


    private void testTokenHolderJsonException(JSONObject jsonObject) {
        try {
            OstTokenHolder ostTokenHolder = new OstTokenHolder(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testTokenHolderJsonWithNoException(JSONObject jsonObject) {
        try {
            OstTokenHolder ostTokenHolder = new OstTokenHolder(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}