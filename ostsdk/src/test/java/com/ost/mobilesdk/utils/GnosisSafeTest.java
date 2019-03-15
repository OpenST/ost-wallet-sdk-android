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

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GnosisSafeTest {


    @Test
    public void testAddOwnerWithThreshold() {

        String expectedOutput = "0x0d582f1300000000000000000000000098443ba43e5a55ff9c0ebeddfd1db32d7b1a" +
                "949a0000000000000000000000000000000000000000000000000000000000000001";

        String encodeMessage = new GnosisSafe().getAddOwnerWithThresholdExecutableData(
                "0x98443bA43e5a55fF9c0EbeDdfd1db32d7b1A949A", "1");

        assertEquals(expectedOutput, encodeMessage);
    }

    @Test
    public void testGetSafeTxData() {
        String expectedOutput = "0x4fd9d0aed661d3993b562981a1cc2f5670723bab7bb45e3ff0c42fc021fa30b4";
        String addOwnerExecutableData = "0x0d582f1300000000000000000000000098443ba43e5a55ff9c0ebeddfd" +
                "1db32d7b1a949a0000000000000000000000000000000000000000000000000000000000000001";
        String NULL_ADDRESS = "0x0000000000000000000000000000000000000000";
        JSONObject safeTxData = new GnosisSafe().getSafeTxData("0x98443bA43e5a55fF9c0EbeDdfd1db32d7b1A949A",
                "0x98443bA43e5a55fF9c0EbeDdfd1db32d7b1A949A"
                ,
                "0", addOwnerExecutableData, "0", "0", "0", "0",
                NULL_ADDRESS,  NULL_ADDRESS, "0");
        String hashString = null;
        try {
             hashString = new EIP712(safeTxData).toEIP712TransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(expectedOutput, hashString);
    }

    @Test
    public void testAddSessionHash() {
        String expectedOutput = "0x204e19fb7d8f765c487d67f0e77460a24804ef10a12d0fbfa668bb9dc036d83b";
        String addOwnerExecutableData = "0x028c979d00000000000000000000000099dbad5becad9eb32eb12a709aaf" +
                "831d1be3b25500000000000000000000000000000000000000000000000000000000000f424000000000" +
                "0000000000000000000000000000000000000000000000174876e800";
        String tokenHolder = "0x59aAF1528a3538752B165EB2D6e0293C86bbCa4F";
        String deviceManager = "0xA5936b94619E1f76349B27879c8B54A118c15A82";
        JSONObject safeTxData = new GnosisSafe.SafeTxnBuilder()
                .setCallData(addOwnerExecutableData)
                .setNonce("4")
                .setToAddress(tokenHolder)
                .setVerifyingContract(deviceManager)
                .build();
        String hashString = null;

        try {
            hashString = new EIP712(safeTxData).toEIP712TransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(expectedOutput, hashString);
    }
}