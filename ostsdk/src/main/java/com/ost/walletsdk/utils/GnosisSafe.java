/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.utils;

import android.util.Log;

import com.ost.walletsdk.OstConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

/**
 * authorizeSession params :_sessionKey address params :_spendingLimit uint256 params :_expirationHeight uint256
 */
public class GnosisSafe {
    private static final String TAG = "OstGnosisSafe";
    private static final String ADD_OWNER_WITH_THRESHHOLD = "addOwnerWithThreshold";
    private static final String AUTHORIZED_SESSION = "authorizeSession";
    private static final String REMOVED_OWNER = "removeOwner";

    public GnosisSafe() {

    }

    public String getAuthorizeSessionExecutableData(String sessionAddress, String spendingLimit, String expirationHeight) {
        Function function = new Function(
                AUTHORIZED_SESSION,  // function we're calling
                Arrays.asList(new Address(sessionAddress), new Uint256(new BigInteger(spendingLimit)), new Uint256(new BigInteger(expirationHeight))),  // Parameters to pass as Solidity Types
                Collections.emptyList());

        return FunctionEncoder.encode(function);
    }

    public String getAuthorizeSessionData(String sessionAddress, String spendingLimit, String expirationHeight) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(OstConstants.METHOD, AUTHORIZED_SESSION);

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(sessionAddress);
            jsonArray.put(spendingLimit);
            jsonArray.put(expirationHeight);

            jsonObject.put(OstConstants.PARAMETERS, jsonArray);
            return jsonObject.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected exception while parsing json");
        }
        return null;
    }

    public String getAddOwnerWithThresholdExecutableData(String ownerAddress, String threshHold) {
        Function function = new Function(
                ADD_OWNER_WITH_THRESHHOLD,  // function we're calling
                Arrays.asList(new Address(ownerAddress), new Uint256(new BigInteger(threshHold))),  // Parameters to pass as Solidity Types
                Collections.emptyList());

        return FunctionEncoder.encode(function);
    }

    public String transactionHash(String deviceManagerAddress, String data, String nonce) throws Exception {
        JSONObject transactionObject = getSafeTxData(deviceManagerAddress, deviceManagerAddress ,"0", data, "0", "0", "0", "0", "0x0000000000000000000000000000000000000000", "0x0000000000000000000000000000000000000000", nonce);
        return new EIP712(transactionObject).toEIP712TransactionHash();
    }

    /**
     * Returns hash in EIP-712 format which is signed by owners.
     *
     * @param to             Destination address of Safe transaction.
     * @param value          Ether value of Safe transaction.
     * @param data           Data payload of Safe transaction.
     * @param operation      Operation type of Safe transaction.
     * @param safeTxGas      Gas that should be used for the Safe transaction.
     * @param dataGas        Gas costs for data used to trigger the safe transaction and to pay the payment transfer
     * @param gasPrice       Gas price that should be used for the payment calculation.
     * @param gasToken       Token address (or 0 if ETH) that is used for the payment.
     * @param refundReceiver Address of receiver of gas payment (or 0 if tx.origin).
     * @param nonce          Transaction nonce.
     * @returns {TypedData}
     */
    public JSONObject getSafeTxData(String to, String verifyingContract ,String value, String data, String operation, String safeTxGas, String dataGas, String gasPrice, String gasToken, String refundReceiver, String nonce) {
        try {
            JSONObject typedDataInput = new JSONObject("{\n" +
                    "      types: {\n" +
                    "        EIP712Domain: [{ name: 'verifyingContract', type: 'address' }],\n" +
                    "        SafeTx: [\n" +
                    "          { name: 'to', type: 'address' },\n" +
                    "          { name: 'value', type: 'uint256' },\n" +
                    "          { name: 'data', type: 'bytes' },\n" +
                    "          { name: 'operation', type: 'uint8' },\n" +
                    "          { name: 'safeTxGas', type: 'uint256' },\n" +
                    "          { name: 'dataGas', type: 'uint256' },\n" +
                    "          { name: 'gasPrice', type: 'uint256' },\n" +
                    "          { name: 'gasToken', type: 'address' },\n" +
                    "          { name: 'refundReceiver', type: 'address' },\n" +
                    "          { name: 'nonce', type: 'uint256' }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      primaryType: 'SafeTx',\n" +
                    "      domain: {\n" +
                    "        verifyingContract: "+ verifyingContract +"\n" +
                    "      },\n" +
                    "      message: {\n" +
                    "        to: " + to + ",\n" +
                    "        value: " + value + ",\n" +
                    "        data: '" + data + "',\n" +
                    "        operation: " + operation + ",\n" +
                    "        safeTxGas: " + safeTxGas + ",\n" +
                    "        dataGas: " + dataGas + ",\n" +
                    "        gasPrice: " + gasPrice + ",\n" +
                    "        gasToken: '" + gasToken + "',\n" +
                    "        refundReceiver: '" + refundReceiver + "',\n" +
                    "        nonce: " + nonce + "\n" +
                    "      }\n" +
                    "    }");
            return typedDataInput;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRemoveOwnerWithThresholdExecutableData(String prevOwner, String ownerToRemove) {
        Function function = new Function(
                REMOVED_OWNER,  // function we're calling
                Arrays.asList(new Address(prevOwner), new Address(ownerToRemove), new Uint256(new BigInteger("1"))),  // Parameters to pass as Solidity Types
                Collections.emptyList());

        return FunctionEncoder.encode(function);
    }

    public String getRemoveOwnerWithThresholdCallData(String prevOwner, String ownerToRemove) {
        String threshold = "1";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(OstConstants.METHOD, REMOVED_OWNER);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(prevOwner);
            jsonArray.put(ownerToRemove);
            jsonArray.put(threshold);
            jsonObject.put(OstConstants.PARAMETERS, jsonArray);

            return jsonObject.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected exception while parsing json");
        }
        return null;
    }

    public static class SafeTxnBuilder {
        private static final String NULL_ADDRESS = "0x0000000000000000000000000000000000000000";
        private String callData = "0x0";
        private String toAddress = "0x0";
        private String value = "0";
        private String operation = "0";
        private String safeTxnGas = "0";
        private String dataGas = "0";
        private String gasPrice = "0";
        private String refundAddress = NULL_ADDRESS;
        private String gasToken = NULL_ADDRESS;
        private String nonce = "0";
        private String verifyingContract = "0x0";

        public SafeTxnBuilder setCallData(String callData) {
            this.callData = callData;
            return this;
        }

        public SafeTxnBuilder setToAddress(String deviceManagerAddress) {
            this.toAddress = deviceManagerAddress;
            return this;
        }

        public SafeTxnBuilder setValue(String value) {
            this.value = value;
            return this;
        }

        public SafeTxnBuilder setOperation(String operation) {
            this.operation = operation;
            return this;
        }

        public SafeTxnBuilder setSafeTxnGas(String safeTxnGas) {
            this.safeTxnGas = safeTxnGas;
            return this;
        }

        public SafeTxnBuilder setDataGas(String dataGas) {
            this.dataGas = dataGas;
            return this;
        }

        public SafeTxnBuilder setGasPrice(String gasPrice) {
            this.gasPrice = gasPrice;
            return this;
        }

        public SafeTxnBuilder setRefundAddress(String refundAddress) {
            this.refundAddress = refundAddress;
            return this;
        }

        public SafeTxnBuilder setGasToken(String gasToken) {
            this.gasToken = gasToken;
            return this;
        }

        public SafeTxnBuilder setNonce(String nonce) {
            this.nonce = nonce;
            return this;
        }

        public SafeTxnBuilder setVerifyingContract(String address) {
            this.verifyingContract = address;
            return this;
        }

        public JSONObject build() {
            return new GnosisSafe().getSafeTxData(toAddress, verifyingContract,
                    value, callData, operation, safeTxnGas, dataGas, gasPrice,
                    gasToken, refundAddress, nonce);
        }
    }

    public String getAddOwnerWithThresholdCallData(String ownerAddress) {
        return getAddOwnerWithThresholdCallData(ownerAddress, "1");
    }

    public String getAddOwnerWithThresholdCallData(String ownerAddress, String threshHold) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(OstConstants.METHOD, ADD_OWNER_WITH_THRESHHOLD);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(ownerAddress);
            jsonArray.put(threshHold);
            jsonObject.put(OstConstants.PARAMETERS, jsonArray);

            return jsonObject.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected exception while parsing json");
        }
        return null;
    }

    public String getAddOwnerWithThresholdExecutableData(String ownerAddress) {
       return getAddOwnerWithThresholdExecutableData(ownerAddress, "1");
    }
}