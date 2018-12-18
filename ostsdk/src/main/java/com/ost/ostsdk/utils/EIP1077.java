package com.ost.ostsdk.utils;

import org.json.JSONObject;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.util.Locale;

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
    JSONObject mTxnHash;

    public EIP1077(JSONObject txnObject) {
        mTxnHash = txnObject;
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

        stringBuilder.append(processSha3Arguments("bytes1", "0x19"));
        stringBuilder.append(processSha3Arguments("bytes1", "0x00"));
        stringBuilder.append(processSha3Arguments("address", mTxnHash.getString(TXN_FROM)));
        stringBuilder.append(processSha3Arguments("address", mTxnHash.getString(TXN_TO)));
        stringBuilder.append(processSha3Arguments("uint8", mTxnHash.getString(TXN_VALUE)));
        stringBuilder.append(processSha3Arguments("bytes32", Hash.sha3(mTxnHash.getString(TXN_DATA))));
        stringBuilder.append(processSha3Arguments("uint256", mTxnHash.getString(TXN_NONCE)));
        stringBuilder.append(processSha3Arguments("uint8", mTxnHash.getString(TXN_GAS_PRICE)));
        stringBuilder.append(processSha3Arguments("uint8", mTxnHash.getString(TXN_GAS)));
        stringBuilder.append(processSha3Arguments("uint8", mTxnHash.getString(TXN_GAS_TOKEN)));
        stringBuilder.append(processSha3Arguments("bytes4", mTxnHash.getString(TXN_CALL_PREFIX)));
        stringBuilder.append(processSha3Arguments("uint8", mTxnHash.getString(TXN_OPERATION_TYPE)));
        stringBuilder.append(processSha3Arguments("bytes32", mTxnHash.getString(TXN_EXTRA_HASH)));

        String sha3Hash = Hash.sha3(stringBuilder.toString());

        return sha3Hash;
    }


    public String processSha3Arguments(String type, String value) throws Exception {
        if (type.startsWith("uint")) {
            int unitBits = Integer.parseInt(type.substring(4));
            int unitNibble = unitBits / 4;
            if (!Numeric.containsHexPrefix(value)) {
                value = Integer.toHexString(Integer.parseInt(value));
            }

            value = Numeric.cleanHexPrefix(value);
            return String.format(String.format(Locale.getDefault(), "%%%ds", unitNibble), value).replace(' ', '0');
        } else if (type.startsWith(("bytes"))) {
            int unitBytes = Integer.parseInt(type.substring(5));
            int unitNibble = unitBytes * 2;
            if (!Numeric.containsHexPrefix(value)) {
                value = Numeric.toHexString(value.getBytes());
            }

            value = Numeric.cleanHexPrefix(value);
            return String.format(String.format(Locale.getDefault(), "%%%ds", unitNibble), value).replace(' ', '0');
        } else if (type.startsWith("address")) {
            if (!Numeric.containsHexPrefix(value)) {
                value = Numeric.toHexString(value.getBytes());
            }
            return Numeric.cleanHexPrefix(value);
        } else {
            throw new Exception("Unknown type provided");
        }
    }
}

