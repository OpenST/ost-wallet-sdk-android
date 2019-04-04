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

import android.content.Context;
import android.text.TextUtils;

import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors.ErrorCode;

import org.json.JSONObject;

import java.io.InputStream;

public class OstConfigs {
    private static OstConfigs sharedInstance = null;

    public static OstConfigs getInstance() {
        return sharedInstance;
    }

    static void init(final Context context) {
        if (null == sharedInstance) {
            sharedInstance = new OstConfigs(context);
        }
    }

    public final long BLOCK_GENERATION_TIME;
    public final long PIN_MAX_RETRY_COUNT;
    public final String PRICE_POINT_TOKEN_SYMBOL;
    public final String PRICE_POINT_CURRENCY_SYMBOL;
    public final long REQUEST_TIMEOUT_DURATION;
    public final long SESSION_BUFFER_TIME;
    public final boolean USE_SEED_PASSWORD;


    private OstConfigs(final Context context) {
        JSONObject config = readConfig(context);
        BLOCK_GENERATION_TIME = config.optLong("BLOCK_GENERATION_TIME", -1);
        if (BLOCK_GENERATION_TIME < 1) {
            throw new OstError("Ost_config_sc_bgt", ErrorCode.INVALID_BLOCK_GENERATION_TIME);
        }

        PIN_MAX_RETRY_COUNT = config.optLong("PIN_MAX_RETRY_COUNT", -1);
        if (PIN_MAX_RETRY_COUNT < 1) {
            throw new OstError("Ost_config_sc_pmrc", ErrorCode.INVALID_PIN_MAX_RETRY_COUNT);
        }

        PRICE_POINT_TOKEN_SYMBOL = config.optString("PRICE_POINT_TOKEN_SYMBOL");
        if (TextUtils.isEmpty(PRICE_POINT_TOKEN_SYMBOL)) {
            throw new OstError("Ost_config_sc_pmrc", ErrorCode.INVALID_PRICE_POINT_TOKEN_SYMBOL);
        }

        PRICE_POINT_CURRENCY_SYMBOL = config.optString("PRICE_POINT_CURRENCY_SYMBOL");
        if (TextUtils.isEmpty(PRICE_POINT_CURRENCY_SYMBOL)) {
            throw new OstError("Ost_config_sc_ppcs", ErrorCode.INVALID_PRICE_POINT_CURRENCY_SYMBOL);
        }

        REQUEST_TIMEOUT_DURATION = config.optLong("REQUEST_TIMEOUT_DURATION", -1);
        if (REQUEST_TIMEOUT_DURATION < 1) {
            throw new OstError("Ost_config_sc_rtd", ErrorCode.INVALID_REQUEST_TIMEOUT_DURATION);
        }

        SESSION_BUFFER_TIME = config.optLong("SESSION_BUFFER_TIME", -1);
        if (SESSION_BUFFER_TIME < 0) {
            throw new OstError("Ost_config_sc_sbt", ErrorCode.INVALID_SESSION_BUFFER_TIME);
        }

        USE_SEED_PASSWORD = config.optBoolean("USE_SEED_PASSWORD", true);
    }

    private JSONObject readConfig(final Context context) {
        try {
            InputStream configInputStream = context.getAssets().open("ost-mobilesdk.json");
            int size = configInputStream.available();
            byte[] buffer = new byte[size];

            configInputStream.read(buffer);
            configInputStream.close();

            String json = new String(buffer, "UTF-8");
            return new JSONObject(json);

        } catch (Exception e) {
            throw new OstError("Ost_config_rc_1", ErrorCode.CONFIG_READ_FAILED);
        }
    }
}