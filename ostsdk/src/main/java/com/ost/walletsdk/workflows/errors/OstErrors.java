/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.workflows.errors;

import android.util.Log;

/**
 * Ost Error with error code and error message
 */
public class OstErrors {

    private static String TAG = "OstErrors";

    public static String getMessage(ErrorCode code) {
        switch (code) {
            case SDK_ERROR: return "An internal SDK error has occurred.";
            case NETWORK_ERROR: return "Request could not be executed due to cancellation, a connectivity problem or timeout.";
            case INVALID_CERTIFICATE: return "Certificate provided by Ost platform is invalid Or it has been compromised. Please re-try in some other network and if the problem persists contact support@ost.com .";
            case INVALID_USER_ID: return "Unable to recognize the user id. Please inspect for what is being sent, rectify and re-submit.";
            case INVALID_SDK_URL: return "Invalid OST server url";
            case INVALID_WORKFLOW_CALLBACK: return "Callback is essential for a workflow to continue running, it cannot be null.";
            case OST_PLATFORM_API_ERROR: return "OST Platform Api returned error.";



            case INVALID_WORKFLOW_PARAMS:
                return "Invalid workflow params. Please ensure the input is well formed or visit https://dev.ost.com/platform/docs/sdk/references for details on workflow parameters.";

            case CREATE_DEVICE_FAILED:
                return "Failed to create device.";

            case DEVICE_NOT_SETUP:
                return "Unable to recognize the device. Please setup this device for the user using workflow provided at https://dev.ost.com/platform/docs/sdk/references";

            case DEVICE_NOT_REGISTERED:
                return "Device is not registered. To make any api to OST server device need to be registered";

            case USER_NOT_ACTIVATED:
                return "The user is not activated yet. Please setup user's wallet to enable their participation in your economy. ";

            case POLLING_TIMEOUT:
                return "Polling timeout. This can be intermittent event with a request failing followed by successful one.";

            case ADD_DEVICE_API_FAILED:
                return "Add device api failed. Either OST server is unavailable temporarily OR The API request object sent cannot be executed. Please re-try and if the problem persists contact support@ost.com .";

            case BLOCK_NUMBER_API_FAILED:
                return "Block number api failed. Either OST server is unavailable temporarily OR The API request object sent cannot be executed. Please re-try and if the problem persists contact support@ost.com . ";

            case EIP712_FAILED:
                return "Unable to sign parameters using EIP 712 and verify the signature.";

            case NO_SESSION_FOUND:
                return "The device doesn't has any active session. Please authorize a session before doing any transaction. Workflow details provided at https://dev.ost.com/platform/docs/sdk/references ";

            case TRANSACTION_API_FAILED:
                return "Transaction api failed. Either OST server is unavailable temporarily OR The API request object sent cannot be executed. Please re-try and if the problem persists contact support@ost.com .";

            case RULE_NOT_FOUND:
                return "Unable to recognize the Rule. Please inspect a valid rule name that exists in your economy is passed and its not null.";

            case INVALID_TOKEN_ID:
                return "The token id sent in Transaction QR code is not matching with the current user's token id. Rectify the value is being sent in token Id field and re-submit the request.";

            case USER_NOT_FOUND:
                return "Unable to find this user in your economy. Inspect if a correct value is being sent in user Id field and re-submit the request. ";

            case CURRENT_DEVICE_NOT_FOUND:
                return "Current device is not registered with the user. Either rectify the value being sent in device Id field OR register this device with the user. ";

            case INVALID_QR_DEVICE_OPERATION_DATA:
                return "The QR code for adding a new device is not well formed. To know the data definition for QR code based on type of operations please visit https://dev.ost.com/platform ";

            case INVALID_ADD_DEVICE_ADDRESS:
                return "Invalid add device address. Please ensure the input is well formed or visit https://dev.ost.com/platform/docs/api for details on accepted datatypes for API parameters.";

            case INVALID_RECOVER_DEVICE_ADDRESS:return "Invalid device address. This address can not be recovered.";

            case DEVICE_UNAUTHORIZED:
                return "Unable to perform the operation as the device not authorized. For details on how to authorize a device please visit https://dev.ost.com/platform/docs/sdk/references ";

            case DEVICE_ALREADY_AUTHORIZED:
                return "This Device is already authorized";

            case USER_ALREADY_ACTIVATED:
                return "The User is already activated";

            case INVALID_MNEMONICS:
                return "The 12 word passphrase you provided is incorrect. ";

            case INVALID_QR_TRANSACTION_DATA:
                return "The QR code for executing a transaction is not well formed. To know the data definition for QR code based on type of operations please visit https://dev.ost.com/platform ";

            case INVALID_USER_PASSPHRASE:
                return "The 6 digit PIN you entered is not correct.";

            case INVALID_NEW_USER_PASSPHRASE:
                return "The new 6 digit PIN you entered is not correct.";

            case INVALID_SESSION_EXPIRY_TIME:
                return "The expiry time provided is invalid";

            case INVALID_SESSION_SPENDING_LIMIT:
                return "The spending limit provided is invalid should be more than 0";

            case MAX_PASSPHRASE_VERIFICATION_LIMIT_REACHED:
                return "The maximum number of 'authenticating with PIN' attempts has been reached. Please try again a bit later.";

            case RECOVERY_PASSPHRASE_OWNER_NOT_SET: return "Recovery owner is not set for this user. This address is set during user activation. Please verify the user has been successfully activated.";

            case RECOVERY_KEY_GENERATION_FAILED: return "Failed to generate Recovery key. Inspect if a correct input values required are being sent and re-submit the request. ";

            case POST_RESET_RECOVERY_API_FAILED:
                return "Reset Recovery api failed. Either OST server is unavailable temporarily OR The API request object sent cannot be executed. Please inspect the input being sent and re-try. If the problem persists contact support@ost.com .";

            case POST_RECOVER_DEVICE_API_FAILED: return "Recover Device API failed. Either OST server is unavailable temporarily OR The API request object sent cannot be executed. Please inspect the input being sent and re-try. If the problem persists contact support@ost.com .";

            case POST_ABORT_RECOVER_DEVICE_API_FAILED: return "Abort Recover Device API failed. Either OST server is unavailable temporarily OR The API request object sent cannot be executed. Please inspect the input being sent and re-try. If the problem persists contact support@ost.com .";

            case UNCAUGHT_EXCEPTION_HANDELED: return "Uncaught exception has been handled. You can choose to report this exception to the OST team for fixing it in future releases.";

            case INSUFFICIENT_DATA: return "The device does not have sufficient data to perform this action.";

            case SESSION_KEY_GENERATION_FAILED: return "Failed to create new session key. Inspect if a correct input values required are being sent and re-submit the request.";

            case FAILED_TO_GENERATE_MESSAGE_HASH: return "Failed to generate message hash needed to complete the action. Inspect if a correct input values required are being sent and re-submit the request.";

            case INVALID_SESSION_ADDRESS: return "Unable to recognize the session address. Inspect if a correct value is being sent and its not null. ";

            case FAILED_TO_SIGN_DATA: return "Unable to sign data. Visit https://dev.ost.com/platform/docs/sdk for detailed SDK references. Please ensure the input is well formed and re-submit the request.";

            case INVALID_DEVICE_ADDRESS : return  "Incorrect device address. Please inspect the value being sent is correct and not null, rectify and re-submit.";

            case DEVICE_CAN_NOT_BE_AUTHORIZED: return "Unable to authorize this device. Please ensure the device is 'Registered' for this user with OST platform. Only a registered device can be authorized.";

            case FAILED_TO_GENERATE_ETH_KEY: return "This is a generic error that occurs when sdk fails to generate any one of Api Key, Device Key or Session Key. This can be intermittent issue, please re-start the workflow. If Problem persists contact support@ost.com .";

            case INVALID_PASSPHRASE_PREFIX: return "Unable to recognize the Passphrase prefix. Please ensure Passphrase prefix is not null or it's string length is not less than 30. ";

            case USER_ACTIVATING: return "User activation flow is already in progress. Please check the status a bit later";

            case USER_PASSPHRASE_VALIDATION_LOCKED: return "Can not validate user passphrase because of too many wrong attempts.";

            case DEVICE_CAN_NOT_BE_REVOKED:
                return "Cannot complete the revoke device operation. Only an authorized device can be revoked. Please ensure you are trying to revoke a valid device and re-submit the request.";

            case EIP1077_FAILED:
                return "EIP1077 failed";

            case UNKNOWN_RULE_NAME:
                return "Unable to recognize the Rule. Please inspect a valid rule name is passed and re-submit the request. ";

            case PRICE_POINTS_API_FAILED:
                return "Price points api failed. Either OST server is unavailable temporarily OR The API request object sent cannot be executed. Please re-try a bit later.";

            case WORKFLOW_CANCELLED:return "Workflow got cancelled, possibly because one or more input parameters require a different type of information.";

            case UNKNOWN_DATA_DEFINITION: return "The QR code does not contain valid data definition. To know the data definition for QR code based on type of operations please visit https://dev.ost.com/platform";

            case DEVICE_ALREADY_REVOKED:
                return "Device is already in revoked state.";

            case INVALID_REVOKE_DEVICE_ADDRESS:
                return "Unable to recoznize revoke device address. Please ensure you are sending a null value and re-submit the request.";
            case NO_PENDING_RECOVERY:
                return "Could not find any pending device recovery request. For details on how to check the status of the recovery please vist https://dev.ost.com/platform/docs/sdk ";
            case CONFIG_READ_FAILED:
                return "Failed to read config file. Please place the ost-sdk config file in main/assets folder.";

            case INVALID_BLOCK_GENERATION_TIME:
                return "Invalid configuration 'BLOCK_GENERATION_TIME'. It must be an Integer greater than zero";
            case INVALID_PIN_MAX_RETRY_COUNT:
                return "Invalid configuration 'PIN_MAX_RETRY_COUNT'. It must be an Integer greater than zero";
            case INVALID_SESSION_BUFFER_TIME:
                return "Invalid configuration 'SESSION_BUFFER_TIME'. It must be long greater than or equal to zero";
            case INVALID_PRICE_POINT_CURRENCY_SYMBOL:
                return "Unable to recognize 'PRICE_POINT_CURRENCY_SYMBOL'. For details on how supported currencies please vist https://dev.ost.com/platform/docs/api ";
            case INVALID_REQUEST_TIMEOUT_DURATION:
                return "Invalid configuration 'REQUEST_TIMEOUT_DURATION'. It must be Integer greater than zero.";

            case LOGOUT_ALL_SESSIONS_FAILED:
                return "Logout request of all sessions failed";

            case INVALID_API_RESPONSE:
                return "Unable to recognize the API response object sent and so cannot be executed.";

            case INVALID_JSON_STRING: return "The provided json string is invalid.";
            case INVALID_JSON_ARRAY: return "The provided json array string is invalid.";

            case WORKFLOW_FAILED:
                return "Something went wrong, please try again";

            //deprecated
            case GET_USER_API_FAILED:
                return "Failed to fetch user information. Either OST server is unavailable temporarily OR your connection is going idle. Check your connection and re-submit the request a bit later.";

            case TOKEN_API_FAILED:
                return "Failed to fetch token information. Either OST server is unavailable temporarily OR your connection is going idle. Check your connection and re-submit the request a bit later.";

            case CHAIN_API_FAILED:
                return "Failed to fetch block-chain information. Either OST server is unavailable temporarily OR your connection is going idle. Check your connection and re-submit the request a bit later.";

            case RULES_API_FAILED:
                return "Failed to fetch rule information. Either OST server is unavailable temporarily OR your connection is going idle. Check your connection and re-submit the request a bit later.";

            case GET_DEVICE_API_FAILED:
                return "Failed to fetch device Information. Either OST server is unavailable temporarily OR your connection is going idle. Check your connection and re-submit the request a bit later.";

            case DEVICE_MANAGER_API_FAILED:
                return "Failed to fetch device manager information. Either OST server is unavailable temporarily OR your connection is going idle. Check your connection and re-submit the request a bit later.";

            case SALT_API_FAILED:
                return "Failed to fetch user salt. Either OST server is unavailable temporarily OR your connection is going idle. Check your connection and re-submit the request a bit later.";

            case POLLING_API_FAILED:
                return "Polling api failed. This can be intermittent event with a request or two failing followed by successful one.";

            case UNKNOWN_ENTITY_TYPE:
                return "Unable to recognize the API request object sent and so cannot be executed.";

            case TOKEN_HOLDER_API_FAILED:
                return "Token holder get api returned failed response";

            case ACTIVATE_USER_API_FAILED:
                return "Unable to activate the user. Inspect if correct input values are being sent, the input is well formed and re-try. If the problem persists contact support@ost.com .";

            case ACTIVATE_USER_API_POLLING_FAILED:
                return "Unable to complete the user activation flow. Inspect if correct input values are being sent, the input is well formed and re-try. If the problem persists contact support@ost.com . ";

            case INVALID_PRICE_POINT_TOKEN_SYMBOL:
                return "Unable to recognize 'PRICE_POINT_TOKEN_SYMBOL'. For details on how supported token symbols please vist https://dev.ost.com/platform/docs/api";

            //Important Note for P.M.:
            //This is a special case. Do not add return in front of UNKNOWN:
            case UNKNOWN:
            default: {
                Log.e(TAG, "Error message not defined for error code " + code.name());
                return "Unknown error";
            }
        }
    }

    public enum ErrorCode {
        SDK_ERROR,
        NETWORK_ERROR,
        INVALID_CERTIFICATE,
        INVALID_API_RESPONSE,
        INVALID_USER_ID,
        INVALID_SDK_URL,
        INVALID_WORKFLOW_CALLBACK,
        DEVICE_NOT_SETUP,
        USER_NOT_ACTIVATED,
        POLLING_TIMEOUT,
        NO_SESSION_FOUND,
        RULE_NOT_FOUND,
        INVALID_TOKEN_ID,
        INVALID_DEVICE_ADDRESS,
        INVALID_RECOVER_DEVICE_ADDRESS,
        DEVICE_UNAUTHORIZED,
        DEVICE_CAN_NOT_BE_AUTHORIZED,
        DEVICE_CAN_NOT_BE_REVOKED,
        INVALID_REVOKE_DEVICE_ADDRESS,
        USER_ALREADY_ACTIVATED,
        INVALID_QR_TRANSACTION_DATA,
        NO_PENDING_RECOVERY,
        DEVICE_NOT_REGISTERED,
        INVALID_SESSION_SPENDING_LIMIT,
        INVALID_SESSION_EXPIRY_TIME,
        INVALID_MNEMONICS,

        //RECOVERY KEY
        RECOVERY_PASSPHRASE_OWNER_NOT_SET,
        INVALID_USER_PASSPHRASE,
        USER_PASSPHRASE_VALIDATION_LOCKED,
        INVALID_NEW_USER_PASSPHRASE,
        INVALID_PASSPHRASE_PREFIX,
        MAX_PASSPHRASE_VERIFICATION_LIMIT_REACHED,
        RECOVERY_KEY_GENERATION_FAILED,

        //Key-Managers
        INSUFFICIENT_DATA,
        INVALID_SESSION_ADDRESS,
        FAILED_TO_SIGN_DATA,


        //Configurations
        INVALID_BLOCK_GENERATION_TIME,
        INVALID_PIN_MAX_RETRY_COUNT,
        INVALID_PRICE_POINT_CURRENCY_SYMBOL,
        INVALID_REQUEST_TIMEOUT_DURATION,
        INVALID_SESSION_BUFFER_TIME,
        INVALID_JSON_STRING,
        INVALID_JSON_ARRAY,

        //Generic
        UNKNOWN,
        WORKFLOW_CANCELLED,
        WORKFLOW_FAILED,
        UNCAUGHT_EXCEPTION_HANDELED,
        FAILED_TO_GENERATE_ETH_KEY,
        OST_PLATFORM_API_ERROR,
        CONFIG_READ_FAILED,

        USER_ACTIVATING,

        //Deprecated
        /**
         * @deprecated GET_USER_API_FAILED has been deprecated.
         */
        GET_USER_API_FAILED,

        /**
         * @deprecated TOKEN_API_FAILED has been deprecated. Kindly check for INVALID_TOKEN_ID
         */
        TOKEN_API_FAILED,

        /**
         * @deprecated GET_DEVICE_API_FAILED has been deprecated.
         */
        GET_DEVICE_API_FAILED,

        /**
         * @deprecated DEVICE_MANAGER_API_FAILED has been deprecated.
         */
        DEVICE_MANAGER_API_FAILED,

        /**
         * @deprecated CHAIN_API_FAILED has been deprecated.
         */
        CHAIN_API_FAILED,

        /**
         * @deprecated RULES_API_FAILED has been deprecated.
         */
        RULES_API_FAILED,

        /**
         * @deprecated ACTIVATE_USER_API_FAILED has been deprecated.
         */
        ACTIVATE_USER_API_FAILED,

        /**
         * @deprecated ACTIVATE_USER_API_FAILED has been deprecated.
         */
        SALT_API_FAILED,

        /**
         * @deprecated ACTIVATE_USER_API_FAILED has been deprecated.
         */
        BLOCK_NUMBER_API_FAILED,

        /**
         * @deprecated POLLING_API_FAILED has been deprecated.
         */
        POLLING_API_FAILED,
        /**
         * @deprecated UNKNOWN_ENTITY_TYPE has been deprecated.
         */
        UNKNOWN_ENTITY_TYPE,

        /**
         * @deprecated TOKEN_HOLDER_API_FAILED has been deprecated.
         */
        TOKEN_HOLDER_API_FAILED,

        /**
         * @deprecated INVALID_PRICE_POINT_TOKEN_SYMBOL has been deprecated.
         */
        INVALID_PRICE_POINT_TOKEN_SYMBOL,

        /**
         * @deprecated POST_ABORT_RECOVER_DEVICE_API_FAILED has been deprecated.
         */
        POST_ABORT_RECOVER_DEVICE_API_FAILED,

        /**
         * @deprecated POST_RECOVER_DEVICE_API_FAILED has been deprecated.
         */
        POST_RECOVER_DEVICE_API_FAILED,

        /**
         * @deprecated PRICE_POINTS_API_FAILED has been deprecated.
         */
        PRICE_POINTS_API_FAILED,

        /**
         * @deprecated INVALID_ADD_DEVICE_ADDRESS has been deprecated.
         */
        INVALID_ADD_DEVICE_ADDRESS,

        /**
         * @deprecated ACTIVATE_USER_API_POLLING_FAILED has been deprecated.
         */
        ACTIVATE_USER_API_POLLING_FAILED,

        /**
         * @deprecated USER_NOT_FOUND has been deprecated.
         */
        USER_NOT_FOUND,

        /**
         * @deprecated CURRENT_DEVICE_NOT_FOUND has been deprecated.
         */
        CURRENT_DEVICE_NOT_FOUND,

        /**
         * @deprecated INVALID_WORKFLOW_PARAMS has been deprecated.
         */
        INVALID_WORKFLOW_PARAMS,

        /**
         * @deprecated CREATE_DEVICE_FAILED has been deprecated.
         */
        CREATE_DEVICE_FAILED,

        /**
         * @deprecated TRANSACTION_API_FAILED has been deprecated.
         */
        TRANSACTION_API_FAILED,

        /**
         * @deprecated EIP712_FAILED has been deprecated.
         */
        EIP712_FAILED,

        /**
         * @deprecated INVALID_QR_DEVICE_OPERATION_DATA has been deprecated.
         */
        INVALID_QR_DEVICE_OPERATION_DATA,

        /**
         * @deprecated DEVICE_ALREADY_AUTHORIZED has been deprecated.
         */
        DEVICE_ALREADY_AUTHORIZED,


        /**
         * @deprecated EIP1077_FAILED has been deprecated.
         */
        EIP1077_FAILED,


        /**
         * @deprecated UNKNOWN_RULE_NAME has been deprecated.
         */
        UNKNOWN_RULE_NAME,


        /**
         * @deprecated UNKNOWN_DATA_DEFINITION has been deprecated.
         */
        UNKNOWN_DATA_DEFINITION,


        /**
         * @deprecated ADD_DEVICE_API_FAILED has been deprecated.
         */
        ADD_DEVICE_API_FAILED,


        /**
         * @deprecated POST_RESET_RECOVERY_API_FAILED has been deprecated.
         */
        POST_RESET_RECOVERY_API_FAILED,

        /**
         * @deprecated LOGOUT_ALL_SESSIONS_FAILED has been deprecated.
         */
        LOGOUT_ALL_SESSIONS_FAILED,


        /**
         * @deprecated SESSION_KEY_GENERATION_FAILED has been deprecated.
         */
        SESSION_KEY_GENERATION_FAILED,

        /**
         * @deprecated FAILED_TO_GENERATE_MESSAGE_HASH has been deprecated.
         */
        FAILED_TO_GENERATE_MESSAGE_HASH,

        /**
         * @deprecated DEVICE_ALREADY_REVOKED has been deprecated.
         */
        DEVICE_ALREADY_REVOKED,
        ;
    }

    public static String errorCodeToString(ErrorCode errorCode) {
        switch (errorCode) {
            case INVALID_SDK_URL: return  "INVALID_API_END_POINT" ;
            case RULE_NOT_FOUND: return "RULES_NOT_FOUND";
            case OST_PLATFORM_API_ERROR: return  "API_RESPONSE_ERROR" ;
            case FAILED_TO_GENERATE_ETH_KEY: return "GENERATE_PRIVATE_KEY_FAIL";
            case NO_SESSION_FOUND : return "SESSION_NOT_FOUND";
            case INVALID_QR_TRANSACTION_DATA : return "INVALID_QR_CODE";
            case RECOVERY_PASSPHRASE_OWNER_NOT_SET : return "RECOVERY_OWNER_ADDRESS_NOT_FOUND";
            case UNCAUGHT_EXCEPTION_HANDELED: return "SDK_ERROR";
            default:
                return errorCode.toString();
        }
    }

}
