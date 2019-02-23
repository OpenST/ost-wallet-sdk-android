package com.ost.mobilesdk.utils;

import com.ost.mobilesdk.OstConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TokenRules {
    private static final String TAG = "OstTokenRules";
    private static final String DIRECT_TRANSFERS = "directTransfers";

    public TokenRules() {
    }

    public String getTransactionExecutableData(List<String> addressListArray, List<String> transferAmountArray) {
        List<Address> addressList = new ArrayList<>();
        List<Uint256> transferAmountList = new ArrayList<>();

        for (String address : addressListArray) {
            addressList.add(new Address(address));
        }
        for (String amount : transferAmountArray) {
            transferAmountList.add(new Uint256(new BigInteger(amount)));
        }
        Function function = new Function(
                "directTransfers",  // function we're calling
                Arrays.asList(new DynamicArray(addressList), new DynamicArray(transferAmountList)),
                Collections.<TypeReference<?>>emptyList());

        return FunctionEncoder.encode(function);
    }

    public String getTransactionRawCallData(List<String> tokenHolderAddresses, List<String> amounts) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OstConstants.METHOD, DIRECT_TRANSFERS);
            CommonUtils commonUtils = new CommonUtils();
            JSONArray addressesArray = commonUtils.listToJSONArray(tokenHolderAddresses);
            JSONArray amountsArray = commonUtils.listToJSONArray(amounts);

            JSONArray parameters = new JSONArray();
            parameters.put(addressesArray);
            parameters.put(amountsArray);

            jsonObject.put(OstConstants.PARAMETERS, parameters);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}