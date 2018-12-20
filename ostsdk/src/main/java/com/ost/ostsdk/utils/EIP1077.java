package com.ost.ostsdk.utils;

import org.json.JSONObject;
import org.web3j.crypto.Hash;

public class EIP1077 {
    public static final String TXN_VALUE = "value";
    public static final String TXN_GAS_PRICE = "gasPrice";
    public static final String TXN_GAS = "gas";
    public static final String TXN_GAS_TOKEN = "gasToken";
    public static final String TXN_OPERATION_TYPE = "operationType";
    public static final String TXN_NONCE = "nonce";
    public static final String TXN_TO = "to";
    public static final String TXN_DATA = "data";
    public static final String TXN_EXTRA_HASH = "extraHash";
    public static final String TXN_FROM = "from";
    public static final String TXN_CALL_PREFIX = "callPrefix";
    private String mVersion;
    JSONObject mTxnHash;

    public EIP1077(JSONObject txnObject, String version) {
        mTxnHash = txnObject;
        mVersion = version;
    }

    public EIP1077(JSONObject jsonObject) {
        this(jsonObject, "0x00");
    }


    public String toEIP1077TransactionHash() throws Exception {
        mTxnHash.put(TXN_VALUE, mTxnHash.optString(TXN_VALUE, "0"));
        mTxnHash.put(TXN_GAS_PRICE, mTxnHash.optString(TXN_GAS_PRICE, "0"));
        mTxnHash.put(TXN_GAS, mTxnHash.optString(TXN_GAS, "0"));
        mTxnHash.put(TXN_GAS_TOKEN, mTxnHash.optString(TXN_GAS_TOKEN, "0"));
        mTxnHash.put(TXN_OPERATION_TYPE, mTxnHash.optString(TXN_OPERATION_TYPE, "0"));
        mTxnHash.put(TXN_NONCE, mTxnHash.optString(TXN_NONCE, "0"));
        mTxnHash.put(TXN_TO, mTxnHash.optString(TXN_TO, "0x0"));
        mTxnHash.put(TXN_FROM, mTxnHash.optString(TXN_FROM, "0x0"));
        mTxnHash.put(TXN_DATA, mTxnHash.optString(TXN_DATA, "0x0"));
        mTxnHash.put(TXN_EXTRA_HASH, mTxnHash.optString(TXN_EXTRA_HASH, "0x0"));
        mTxnHash.put(TXN_CALL_PREFIX, mTxnHash.optString(TXN_CALL_PREFIX, "0x0"));

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("0x");

        String sha3Hash = new SoliditySha3().soliditySha3(
                new JSONObject(String.format("{ t: 'bytes', v: '%s' }", "0x19")),
                new JSONObject(String.format("{ t: 'bytes', v: '%s' }", mVersion)),
                new JSONObject(String.format("{ t: 'address', v: '%s' }", mTxnHash.getString(TXN_FROM))),
                new JSONObject(String.format("{ t: 'address', v: '%s' }", mTxnHash.getString(TXN_TO))),
                new JSONObject(String.format("{ t: 'uint8', v: '%s' }", mTxnHash.getString(TXN_VALUE))),
                new JSONObject(String.format("{ t: 'bytes', v:'%s' }", Hash.sha3(mTxnHash.getString(TXN_DATA)))),
                new JSONObject(String.format("{ t: 'uint256', v: '%s' }", mTxnHash.getString(TXN_NONCE))),
                new JSONObject(String.format("{ t: 'uint8', v: '%s' }", mTxnHash.getString(TXN_GAS_PRICE))),
                new JSONObject(String.format("{ t: 'uint8', v: '%s' }", mTxnHash.getString(TXN_GAS))),
                new JSONObject(String.format("{ t: 'uint8', v: '%s' }", mTxnHash.getString(TXN_GAS_TOKEN))),
                new JSONObject(String.format("{ t: 'bytes4', v: '%s' }", mTxnHash.getString(TXN_CALL_PREFIX))),
                new JSONObject(String.format("{ t: 'uint8', v: '%s' }", mTxnHash.getString(TXN_OPERATION_TYPE))),
                new JSONObject(String.format("{ t: 'bytes32', v: '%s' }", mTxnHash.getString(TXN_EXTRA_HASH)))
        );

        return sha3Hash;
    }
}

