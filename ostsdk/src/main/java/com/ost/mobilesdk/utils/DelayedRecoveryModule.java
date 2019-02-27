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
}