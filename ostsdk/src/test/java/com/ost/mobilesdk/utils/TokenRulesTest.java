/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TokenRulesTest {


    @Test
    public void testDirectTransferCallData() {

        String expectedOutput = "0x94ac7a3f0000000000000000000000000000000000000000000000000000000000" +
                "00004000000000000000000000000000000000000000000000000000000000000000800000000000000" +
                "0000000000000000000000000000000000000000000000000010000000000000000000000004a523bf8" +
                "cd96323e315df9442c6d04880c6fafb50000000000000000000000000000000000000000000000000000" +
                "00000000000100000000000000000000000000000000000000000000000000000002540be400";

        List<String> tokenHolderAddresses = Arrays.asList("0x4a523bf8cd96323e315df9442c6d04880c6fafb5");
        List<String> amounts = Arrays.asList("10000000000");
        String encodeMessage = new TokenRules().getTransactionExecutableData(tokenHolderAddresses, amounts);

        assertEquals(expectedOutput, encodeMessage);
    }
}