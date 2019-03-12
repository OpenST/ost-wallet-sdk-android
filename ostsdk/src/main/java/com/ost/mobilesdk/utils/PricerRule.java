package com.ost.mobilesdk.utils;

import com.ost.mobilesdk.OstConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Bytes3;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PricerRule {
    private static final String PAY = "pay";

    public String getPriceTxnExecutableData(String fromAddress, List<String> addressListArray, List<String> amounts, String currencyCode, BigInteger pricePoint) {
        List<Address> addressList = new ArrayList<>();
        List<Uint256> transferAmountList = new ArrayList<>();

        for (String address : addressListArray) {
            addressList.add(new Address(address));
        }
        for (String amount : amounts) {
            transferAmountList.add(new Uint256(new BigInteger(amount)));
        }
        Function function = new Function(
                PAY,  // function we're calling
                Arrays.asList(new Address(fromAddress), new DynamicArray(addressList),
                        new DynamicArray(transferAmountList), new Bytes3(currencyCode.getBytes()),
                        new Uint256(pricePoint)),
                Collections.emptyList());

        return FunctionEncoder.encode(function);
    }

    public String getPricerTransactionRawCallData(String fromAddress, List<String> tokenHolderAddresses, List<String> amounts, String currencyCode, BigInteger pricePoint) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OstConstants.METHOD, PAY);
            CommonUtils commonUtils = new CommonUtils();
            JSONArray addressesArray = commonUtils.listToJSONArray(tokenHolderAddresses);
            JSONArray amountsArray = commonUtils.listToJSONArray(amounts);

            JSONArray parameters = new JSONArray();
            parameters.put(fromAddress);
            parameters.put(addressesArray);
            parameters.put(amountsArray);
            parameters.put(currencyCode);
            parameters.put(pricePoint.toString());

            jsonObject.put(OstConstants.PARAMETERS, parameters);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String calDirectTransferSpendingLimit(List<String> amounts, BigInteger fiatMultiplier) {
        BigInteger bigInteger = BigInteger.ZERO;
        for (String amount : amounts) {
            BigInteger btFiaEquivalent = fiatMultiplier.multiply(new BigInteger(amount));
            bigInteger = bigInteger.add(btFiaEquivalent);
        }
        return bigInteger.toString();
    }
}