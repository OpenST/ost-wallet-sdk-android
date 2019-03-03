package com.ost.mobilesdk.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class DelayedRecoveryModule {

    private static final String TAG = "OstDelayedRecovery";

    public DelayedRecoveryModule() {
    }

    public JSONObject resetRecoveryOwnerData(String oldRecoveryOwnerAddress, String newRecoveryOwnerAddress, String recoveryAddress) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject("{\n" +
                    "      types: {\n" +
                    "        EIP712Domain: [{ name: 'verifyingContract', type: 'address' }],\n" +
                    "        ResetRecoveryOwnerStruct: [\n" +
                    "          { name: 'oldRecoveryOwner', type: 'address' },\n" +
                    "          { name: 'newRecoveryOwner', type: 'address' }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      primaryType: 'ResetRecoveryOwnerStruct',\n" +
                    "      domain: {\n" +
                    "        verifyingContract: " + recoveryAddress + " \n" +
                    "      },\n" +
                    "      message: {\n" +
                    "        oldRecoveryOwner: " + oldRecoveryOwnerAddress + ",\n" +
                    "        newRecoveryOwner: " + newRecoveryOwnerAddress + "\n" +
                    "      }\n" +
                    "    }");
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception");
            return null;
        }

        return jsonObject;
    }

    public JSONObject generateInitiateRecoveryOwnerData(String prevOwnerAddress, String oldOwnerAddress,
                                                        String newOwnerAddress, String recoveryAddress) {
        return recoveryOwnerData(prevOwnerAddress, oldOwnerAddress, newOwnerAddress, recoveryAddress,
                "InitiateRecoveryStruct");

    }

    public JSONObject generateAbortRecoveryOwnerData(String prevOwnerAddress, String oldOwnerAddress,
                                                     String newOwnerAddress, String recoveryAddress) {
        return recoveryOwnerData(prevOwnerAddress, oldOwnerAddress, newOwnerAddress, recoveryAddress,
                "AbortRecoveryStruct");

    }

    private JSONObject recoveryOwnerData(String prevOwnerAddress, String oldOwnerAddress, String newOwnerAddress,
                                         String recoveryAddress, String primaryType) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject("const typedDataInput = {\n" +
                    "      types: {\n" +
                    "        EIP712Domain: [{ name: 'verifyingContract', type: 'address' }],\n" +
                    "        " + primaryType + ": [\n" +
                    "          { name: 'prevOwner', type: 'address' },\n" +
                    "          { name: 'oldOwner', type: 'address' },\n" +
                    "          { name: 'newOwner', type: 'address' }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      primaryType: '" + primaryType + "',\n" +
                    "      domain: {\n" +
                    "        verifyingContract: " + recoveryAddress + "\n" +
                    "      },\n" +
                    "      message: {\n" +
                    "        prevOwner: " + prevOwnerAddress + ",\n" +
                    "        oldOwner: " + oldOwnerAddress + ",\n" +
                    "        newOwner: " + newOwnerAddress + "\n" +
                    "      }\n" +
                    "    }");
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception");
            return null;
        }

        return jsonObject;
    }
}