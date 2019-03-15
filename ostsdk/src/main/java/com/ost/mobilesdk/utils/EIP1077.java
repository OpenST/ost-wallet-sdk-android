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

import org.json.JSONException;
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

    public static class TransactionBuilder {

        private String data = "0x0";
        private String value = "0";
        private String gasPrice = "0";
        private String gas = "0";
        private String gasToken = "0";
        private String operationType = "0";
        private String nonce = "0";
        private String to = "0x0";
        private String from = "0x0";
        private String extraHash = "0x0";
        private String callPrefix = "0x0";

        public TransactionBuilder setData(String data) {
            this.data = data;
            return this;
        }

        public TransactionBuilder setValue(String value) {
            this.value = value;
            return this;
        }

        public TransactionBuilder setGasPrice(String gasPrice) {
            this.gasPrice = gasPrice;
            return this;
        }

        public TransactionBuilder setGas(String gas) {
            this.gas = gas;
            return this;
        }

        public TransactionBuilder setGasToken(String gasToken) {
            this.gasToken = gasToken;
            return this;
        }

        public TransactionBuilder setOperationType(String operationType) {
            this.operationType = operationType;
            return this;
        }

        public TransactionBuilder setNonce(String nonce) {
            this.nonce = nonce;
            return this;
        }

        public TransactionBuilder setTo(String to) {
            this.to = to;
            return this;
        }

        public TransactionBuilder setFrom(String from) {
            this.from = from;
            return this;
        }

        public TransactionBuilder setExtraHash(String extraHash) {
            this.extraHash = extraHash;
            return this;
        }

        public TransactionBuilder setCallPrefix(String callPrefix) {
            this.callPrefix = callPrefix;
            return this;
        }

        public JSONObject build() throws JSONException {
            JSONObject txnObject = new JSONObject();
            txnObject.put(TXN_VALUE, value);
            txnObject.put(TXN_GAS_PRICE, gasPrice);
            txnObject.put(TXN_GAS, gas);
            txnObject.put(TXN_GAS_TOKEN, gasToken);
            txnObject.put(TXN_OPERATION_TYPE, operationType);
            txnObject.put(TXN_NONCE, nonce);
            txnObject.put(TXN_TO, to);
            txnObject.put(TXN_FROM, from);
            txnObject.put(TXN_DATA, data);
            txnObject.put(TXN_EXTRA_HASH, extraHash);
            txnObject.put(TXN_CALL_PREFIX, callPrefix);
            return txnObject;
        }
    }
}

