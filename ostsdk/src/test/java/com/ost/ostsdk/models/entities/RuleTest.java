package com.ost.ostsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RuleTest {

    @Test
    public void testRuleInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any Rule  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        testRuleJsonException(jsonObject);

        //Test Id with partial Rule attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(Rule.ADDRESS, "0x232");
        jsonObject.put(Rule.ABI, "abi");
        testRuleJsonException(jsonObject);
    }

    @Test
    public void testRuleValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any Rule  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(Rule.ADDRESS, "0x2901239");
        jsonObject.put(Rule.NAME, "name");
        jsonObject.put(Rule.ABI, "abi");
        jsonObject.put(Rule.ECONOMY_ID, "123");

        Rule rule = new Rule(jsonObject);
        assertEquals("0x2901239", rule.getAddress());
        assertEquals("name", rule.getName());
        assertEquals("abi", rule.getAbi());
        assertEquals("123", rule.getEconomyId());
        assertEquals("ID", rule.getId());

    }


    private void testRuleJsonException(JSONObject jsonObject) {
        try {
            Rule rule = new Rule(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testRuleJsonWithNoException(JSONObject jsonObject) {
        try {
            Rule rule = new Rule(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}