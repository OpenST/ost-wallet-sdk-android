package com.ost.ostsdk.models.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TokenHolderTest {

    @Test
    public void testTokenHolderInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any TokenHolder  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        testTokenHolderJsonException(jsonObject);

        //Test Id with partial TokenHolder attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(TokenHolder.ADDRESS, "0x232");
        jsonObject.put(TokenHolder.REQUIREMENTS, 1);
        testTokenHolderJsonException(jsonObject);
    }

    @Test
    public void testTokenHolderValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any TokenHolder  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(TokenHolder.ADDRESS, "0x2901239");
        jsonObject.put(TokenHolder.REQUIREMENTS, 1);
        jsonObject.put(TokenHolder.USER_ID, "123");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("0x12345678");
        jsonObject.put(TokenHolder.EXECUTE_RULE_CALL_PREFIX, "callPrefix");


        TokenHolder tokenHolder = new TokenHolder(jsonObject);
        assertEquals("0x2901239", tokenHolder.getAddress());
        assertEquals(1, tokenHolder.getRequirements());
        assertEquals("123", tokenHolder.getUserId());
        assertEquals("callPrefix", tokenHolder.getExecuteRuleCallPrefix());
        assertEquals("ID", tokenHolder.getId());

    }


    private void testTokenHolderJsonException(JSONObject jsonObject) {
        try {
            TokenHolder tokenHolder = new TokenHolder(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testTokenHolderJsonWithNoException(JSONObject jsonObject) {
        try {
            TokenHolder tokenHolder = new TokenHolder(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}