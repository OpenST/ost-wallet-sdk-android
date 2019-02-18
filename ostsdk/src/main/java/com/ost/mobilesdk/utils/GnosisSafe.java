package com.ost.mobilesdk.utils;

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
                    "        gasToken: " + gasToken + ",\n" +
                    "        refundReceiver: " + refundReceiver + ",\n" +
                    "        nonce: " + nonce + "\n" +
                    "      }\n" +
                    "    }");
            return typedDataInput;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> getAddOwnerWithThresholdData(String ownerAddress, String threshHold) {
        Map<String,Object> map = new HashMap<>();
        map.put("method", "addOwnerWithThreshold");
        List<String> paramList = Arrays.asList(ownerAddress, threshHold);
        map.put("parameters", paramList);
        return map;
    }
}