/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk;

public interface OstConstants {
    String RESPONSE_SUCCESS = "success";
    String RESPONSE_DATA = "data";
    String RESULT_TYPE = "result_type";

    String SESSION_ADDRESS = "session_address";

    int THREAD_POOL_SIZE = 5;
    String BLOCK_HEIGHT = "block_height";
    String BLOCK_TIME = "block_time";
    String USER_ID = "user_id";
    String TOKEN_ID = "token_id";
    String METHOD = "method";
    String PARAMETERS = "parameters";
    String TRANSACTION_OPTIONS = "transaction_options";
    String QR_DATA = "d";
    String QR_META = "m";
    String QR_DATA_DEFINITION = "dd";
    String QR_DATA_DEFINITION_VERSION = "ddv";
    String QR_DEVICE_ADDRESS = "da";
    String QR_SESSION_DATA = "sd";
    String QR_SESSION_ADDRESS = "sa";
    String QR_SPENDING_LIMIT = "sl";
    String QR_EXPIRY_TIMESTAMP = "et";
    String QR_SIGNATURE = "sig";
    String QR_V2_INPUT = "qr_v2_input";
    String QR_V2_DELIMITER = "|";
    String DATA_DEFINITION_TRANSACTION = "TX";
    String DATA_DEFINITION_AUTHORIZE_DEVICE = "AD";
    String DATA_DEFINITION_REVOKE_DEVICE = "RD";
    String DATA_DEFINITION_AUTHORIZE_SESSION = "AS";
    String QR_RULE_NAME = "rn";
    String QR_TOKEN_HOLDER_ADDRESSES = "ads";
    String QR_AMOUNTS = "ams";
    String QR_TOKEN_ID = "tid";
    String QR_OPTIONS_DATA = "o";
    String QR_CURRENCY_CODE = "cs";
    String QR_CURRENCY_SIGN = "s";
    String META_TRANSACTION_NAME = "name";
    String META_TRANSACTION_TYPE = "type";
    String META_TRANSACTION_DETAILS = "details";
    String QR_META_TRANSACTION_NAME = "tn";
    String QR_META_TRANSACTION_TYPE = "tt";
    String QR_META_TRANSACTION_DETAILS = "td";

    String RULE_NAME = "rule_name";
    String TOKEN_HOLDER_ADDRESSES = "token_holder_addresses";
    String AMOUNTS = "amounts";
    String DEVICE_ADDRESS = "device_address";

    int RECOVERY_PHRASE_PREFIX_MIN_LENGTH = 30;
    int RECOVERY_PHRASE_USER_INPUT_MIN_LENGTH = 6;
    long POLLING_WAIT_TIME_IN_SECS = 60 * 2;

    int BUILD_VERSION_CODE = BuildConfig.VERSION_CODE;
    String BUILD_VERSION_NAME = BuildConfig.VERSION_NAME;
    String OST_API_VERSION = "2";
    String USER_AGENT = String.format("ost-sdk-android-%s-%s", OST_API_VERSION, BUILD_VERSION_NAME);
    String CONTENT_TYPE = "application/x-www-form-urlencoded";
}