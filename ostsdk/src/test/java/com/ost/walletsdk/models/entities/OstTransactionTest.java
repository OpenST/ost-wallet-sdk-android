/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OstTransactionTest {

    @Test
    public void testExecutableRuleInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject = new JSONObject();

        //Test without any OstTransaction  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstTransaction.TRANSACTION_HASH, "ID");
        testExecutableRuleJsonException(jsonObject);

        //Test Id with partial OstTransaction attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstTransaction.TRANSACTION_HASH, "ID");
        jsonObject.put(OstTransaction.TRANSFERS, "{}");
        jsonObject.put(OstTransaction.TRANSACTION_FEE, "methods");
        jsonObject.put(OstTransaction.BLOCK_TIMESTAMP, "params");
        testExecutableRuleJsonException(jsonObject);
    }

    @Test
    public void testExecutableRuleValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any OstTransaction  attribute
        jsonObject = new JSONObject();
        jsonObject.put(OstTransaction.TRANSFERS, new JSONObject());
        jsonObject.put(OstTransaction.TRANSACTION_FEE, "methods");
        jsonObject.put(OstTransaction.BLOCK_TIMESTAMP, "params");
        jsonObject.put(OstTransaction.GAS_PRICE, "2323");
        jsonObject.put(OstTransaction.BLOCK_NUMBER, "session");
        jsonObject.put(OstTransaction.STATUS, "status");
        jsonObject.put(OstTransaction.GAS_USED, "0x323W");
        jsonObject.put(OstTransaction.TRANSACTION_HASH, "123");
        jsonObject.put(OstTransaction.RULE_NAME, "rule");
        jsonObject.put(OstTransaction.TRANSFERS, "1");
        testMultiSigJsonWithNoException(jsonObject);

        //Test Id with partial OstTransaction attribute
        OstTransaction executableRule = new OstTransaction(jsonObject);
        assertEquals("methods", executableRule.getTransactionFee());
        assertEquals("params", executableRule.getBlockTimestamp());
        assertEquals("0x323W", executableRule.getGasUsed());
        assertEquals("session", executableRule.getBlockNumber());
        assertEquals("status", executableRule.getStatus());
        assertEquals("2323", executableRule.getGasPrice());
        assertEquals("123", executableRule.getTransactionHash());
        assertEquals("rule", executableRule.getRuleName());
        assertEquals("1", executableRule.getTransfers());

    }


    private void testExecutableRuleJsonException(JSONObject jsonObject) {
        try {
            OstTransaction baseEntity = new OstTransaction(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testMultiSigJsonWithNoException(JSONObject jsonObject) {
        try {
            OstTransaction baseEntity = new OstTransaction(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}