package com.ost.ostsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExecutableRuleTest {

    @Test
    public void testExecutableRuleInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject = new JSONObject();

        //Test without any ExecutableRule  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        testExecutableRuleJsonException(jsonObject);

        //Test Id with partial ExecutableRule attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(ExecutableRule.EXECUTE_RULE_PAYLOAD, "{}");
        jsonObject.put(ExecutableRule.METHOD, "methods");
        jsonObject.put(ExecutableRule.PARAMS, "params");
        testExecutableRuleJsonException(jsonObject);
    }

    @Test
    public void testExecutableRuleValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any ExecutableRule  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(ExecutableRule.EXECUTE_RULE_PAYLOAD, new JSONObject());
        jsonObject.put(ExecutableRule.METHOD, "methods");
        jsonObject.put(ExecutableRule.PARAMS, "params");
        jsonObject.put(ExecutableRule.RULE_ID, "2323");
        jsonObject.put(ExecutableRule.SESSION, "session");
        jsonObject.put(ExecutableRule.STATUS, "status");
        jsonObject.put(ExecutableRule.TOKEN_HOLDER_ADDRESS, "0x323W");
        jsonObject.put(ExecutableRule.USER_ID, "123");
        testMultiSigJsonWithNoException(jsonObject);

        //Test Id with partial ExecutableRule attribute
        ExecutableRule executableRule = new ExecutableRule(jsonObject);
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
            ExecutableRule baseEntity = new ExecutableRule(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testMultiSigJsonWithNoException(JSONObject jsonObject) {
        try {
            ExecutableRule baseEntity = new ExecutableRule(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}