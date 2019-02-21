package com.ost.mobilesdk.workflows.errors;

import android.util.Log;

public class OstErrors {
    private static String TAG = "OstErrors";
    public static enum ErrorCode {
        UNKNOWN,
        USER_API_FAILED,
        TOKEN_API_FAILED,
        CHAIN_API_FAILED,
        SALT_API_FAILED,
        INVALID_WORKFLOW_PARAMS,
        CREATE_DEVICE_FAILED,
        ACTIVATE_USER_API_FAILED,
        ACTIVATE_USER_API_POLLING_FAILED,
        DEVICE_UNREGISTERED,
        WORKFLOW_CANCELED,
        USER_NOT_ACTIVATED,
        POLLING_TIMEOUT,
        MAX_PIN_LIMIT_REACHED,
        BLOCK_NUMBER_API_FAILED,
        ADD_DEVICE_API_FAILED,
        EIP712_FAILED;
    }

    public static String getMessage(ErrorCode code) {
        switch (code) {
            case USER_API_FAILED: return "Failed to fetch user information";

            case SALT_API_FAILED: return "Failed to fetch user salt";

            case TOKEN_API_FAILED: return "Failed to fetch token information";

            case CHAIN_API_FAILED: return "Failed to fetch block-chain information";

            case INVALID_WORKFLOW_PARAMS: return "Invalid workflow params";

            case CREATE_DEVICE_FAILED: return "Failed to create device.";

            case ACTIVATE_USER_API_FAILED: return "Something went wrong while activating user";

            case ACTIVATE_USER_API_POLLING_FAILED: return "Something went wrong while activating user";

            case DEVICE_UNREGISTERED: return "Device not registered";

            case WORKFLOW_CANCELED: return "Workflow canceled by application";

            case USER_NOT_ACTIVATED: return "User not activated";

            case POLLING_TIMEOUT: return "Polling timeout";

            case MAX_PIN_LIMIT_REACHED: return "Max pin ask limit reached";

            case ADD_DEVICE_API_FAILED: return "Add device api failed";

            case BLOCK_NUMBER_API_FAILED: return "Block number api failed";

            case EIP712_FAILED: return "EIP712 failed";

            //Important Note for P.M.:
            //This is a special case. Do not add return in front of UNKNOWN:
            case UNKNOWN:
            default: {
                Log.e(TAG, "Error message not defined for error code " + code.name() );
                return "Unknown error";
            }
        }
    }
}
