package com.ost.ostsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OstExecutableRuleTest {

    @Test
    public void testExecutableRuleInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject = new JSONObject();

        //Test without any OstExecutableRule  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        testExecutableRuleJsonException(jsonObject);

        //Test Id with partial OstExecutableRule attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstExecutableRule.EXECUTE_RULE_PAYLOAD, "{}");
        jsonObject.put(OstExecutableRule.METHOD, "methods");
        jsonObject.put(OstExecutableRule.PARAMS, "params");
        testExecutableRuleJsonException(jsonObject);
    }

    @Test
    public void testExecutableRuleValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstExecutableRule  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.ID, "ID");
        jsonObject.put(OstExecutableRule.EXECUTE_RULE_PAYLOAD, new JSONObject());
        jsonObject.put(OstExecutableRule.METHOD, "methods");
        jsonObject.put(OstExecutableRule.PARAMS, "params");
        jsonObject.put(OstExecutableRule.RULE_ID, "2323");
        jsonObject.put(OstExecutableRule.SESSION, "session");
        jsonObject.put(OstExecutableRule.STATUS, "status");
        jsonObject.put(OstExecutableRule.TOKEN_HOLDER_ADDRESS, "0x323W");
        jsonObject.put(OstExecutableRule.USER_ID, "123");
        testMultiSigJsonWithNoException(jsonObject);

        //Test Id with partial OstExecutableRule attribute
        OstExecutableRule executableRule = new OstExecutableRule(jsonObject);
        assertEquals("{}", executableRule.getExecuteRulePayload().toString());
        assertEquals("methods", executableRule.getMethod());
        assertEquals("params", executableRule.getParams());
        assertEquals("2323", executableRule.getRuleId());
        assertEquals("session", executableRule.getSession());
        assertEquals("status", executableRule.getStatus());
        assertEquals("0x323W", executableRule.getTokenHolderAddress());
        assertEquals("123", executableRule.getUserId());

    }


    private void testExecutableRuleJsonException(JSONObject jsonObject) {
        try {
            OstExecutableRule baseEntity = new OstExecutableRule(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testMultiSigJsonWithNoException(JSONObject jsonObject) {
        try {
            OstExecutableRule baseEntity = new OstExecutableRule(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}