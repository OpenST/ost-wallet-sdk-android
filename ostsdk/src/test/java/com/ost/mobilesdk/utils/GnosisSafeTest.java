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
}