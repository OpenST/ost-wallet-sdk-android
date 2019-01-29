package com.ost.ostsdk.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EIP712Test {
    private static final String MSG_HASH = "0xbe609aee343fb3c4b28e1df9e632fca64fcfaede20f02e86244efddf30957bd2";
    private static final String EncodedMailType =  "Mail(Person from,Person to,string contents)Person(string name,address wallet)";
    private static final String MailTypeHash = "0xa0cedeb2dc280ba39b857546d74f5549c3a1d7bdc2dd96bf881f76108e23dac2";
    private static final String EncodedData = "0xa0cedeb2dc280ba39b857546d74f5549c3a1d7bdc2dd96bf881f76108e23dac2fc71e5fa27ff56c350aa531bc129ebdf613b772b6604664f5d8dbe21b85eb0c8cd54f074a4af31b4411ff6a60c9719dbd559c221c8ac3492d9d872b041d703d1b5aadf3154a261abdd9086fc627b61efca26ae5702701d05cd2305f7c52a2fc8";
    private static JSONObject TypedDataInput;

    static {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/testJSON.js"));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            TypedDataInput = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEIP712TransactionConversion() {

        try {
            String eip1077Hash = new EIP712(TypedDataInput).toEIP712TransactionHash();
            assertEquals(MSG_HASH, eip1077Hash);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testEncodeDataType() {
        try {
            String encodeDataType = new EIP712(TypedDataInput).encodeDataType("Mail");
            assertEquals(EncodedMailType, encodeDataType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHashDataType() {
        try {
            String encodeDataType = new EIP712(TypedDataInput).hashDataType("Mail");
            assertEquals(MailTypeHash, encodeDataType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEncodeData() {
        try {
            String encodeDataType = new EIP712(TypedDataInput).encodeData("Mail",TypedDataInput.optJSONObject("message"));
            assertEquals(EncodedData, encodeDataType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}