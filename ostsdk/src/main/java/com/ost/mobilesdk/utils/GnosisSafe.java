package com.ost.mobilesdk.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GnosisSafe {
    private static final String TAG = "OstGnosisSafe";
    public GnosisSafe() {

    }

    public String getAddOwnerWithThresholdExecutableData(String ownerAddress, String threshHold) {
        Function function = new Function(
                "addOwnerWithThreshold",  // function we're calling
                Arrays.asList(new Address(ownerAddress), new Uint256(new BigInteger(threshHold))),  // Parameters to pass as Solidity Types
                Collections.<TypeReference<?>>emptyList());

        return FunctionEncoder.encode(function);
    }

    public String transactionHash(String deviceManagerAddress, String data, String nonce) throws Exception {
        JSONObject transactionObject = getSafeTxData(deviceManagerAddress, "0", data, "0", "0", "0", "0", "0x0000000000000000000000000000000000000000", "0x0000000000000000000000000000000000000000", nonce);
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
    public JSONObject getSafeTxData(String to, String value, String data, String operation, String safeTxGas, String dataGas, String gasPrice, String gasToken, String refundReceiver, String nonce) {
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
                    "        verifyingContract: "+ to +"\n" +
                    "      },\n" +
                    "      message: {\n" +
                    "        to: " + to + ",\n" +
                    "        value: " + value + ",\n" +
                    "        data: " + data + ",\n" +
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

    public JSONObject getJSONAddOwnerWithThresholdData(String ownerAddress) {
        return getJSONAddOwnerWithThresholdData(ownerAddress, "1");
    }

    public static class SafeTxnBuilder {
        private static final String NULL_ADDRESS = "0x0000000000000000000000000000000000000000";
        private String addOwnerExecutableData = "0x0";
        private String deviceManagerAddress = "0x0";
        private String value = "0";
        private String operation = "0";
        private String safeTxnGas = "0";
        private String dataGas = "0";
        private String gasPrice = "0";
        private String refundAddress = NULL_ADDRESS;
        private String gasToken = NULL_ADDRESS;
        private String nonce = "0";

        public SafeTxnBuilder setAddOwnerExecutableData(String addOwnerExecutableData) {
            this.addOwnerExecutableData = addOwnerExecutableData;
            return this;
        }

        public SafeTxnBuilder setDeviceManagerAddress(String deviceManagerAddress) {
            this.deviceManagerAddress = deviceManagerAddress;
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

        public JSONObject build() {
            return new GnosisSafe().getSafeTxData(deviceManagerAddress,
                    value, addOwnerExecutableData, operation, safeTxnGas, dataGas, gasPrice,
                    gasToken, refundAddress, nonce);
        }
    }

    public Map<String, Object> getAddOwnerWithThresholdData(String ownerAddress, String threshHold) {
        Map<String,Object> map = new HashMap<>();
        map.put("method", "addOwnerWithThreshold");
        List<String> paramList = Arrays.asList(ownerAddress, threshHold);
        map.put("parameters", paramList);
        return map;
    }

    public JSONObject getJSONAddOwnerWithThresholdData(String ownerAddress, String threshHold) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("method", "addOwnerWithThreshold");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(ownerAddress);
            jsonArray.put(threshHold);
            jsonObject.put("parameters", jsonArray);

            return jsonObject;
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected exception while parsing json");
        }
        return null;
    }

    public String getAddOwnerWithThresholdExecutableData(String ownerAddress) {
       return getAddOwnerWithThresholdExecutableData(ownerAddress, "1");
    }
}