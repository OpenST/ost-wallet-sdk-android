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
import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors.ErrorCode;

import org.json.JSONObject;

import java.io.InputStream;

public class OstConfigs {
    private static final String TAG = "OstConfigs";
    private static OstConfigs sharedInstance = null;

    public static OstConfigs getInstance() {
        return sharedInstance;
    }

    static void init(final Context context, JSONObject config) {
        sharedInstance = new OstConfigs(context, config);
    }

    public long getBLOCK_GENERATION_TIME() {
        return 0 > BLOCK_GENERATION_TIME ? 3 : BLOCK_GENERATION_TIME;
    }

    public long getPIN_MAX_RETRY_COUNT() {
        return 0 > PIN_MAX_RETRY_COUNT ? 3 : PIN_MAX_RETRY_COUNT;
    }

    public String getPRICE_POINT_CURRENCY_SYMBOL() {
        return TextUtils.isEmpty(PRICE_POINT_CURRENCY_SYMBOL) ? "USD" : PRICE_POINT_CURRENCY_SYMBOL;
    }

    public long getREQUEST_TIMEOUT_DURATION() {
        return 0 > REQUEST_TIMEOUT_DURATION ? 60 : REQUEST_TIMEOUT_DURATION;
    }

    public long getSESSION_BUFFER_TIME() {
        return 0 > SESSION_BUFFER_TIME ? 3600 : SESSION_BUFFER_TIME;
    }

    public boolean isUSE_SEED_PASSWORD() {
        return USE_SEED_PASSWORD;
    }

    public int getNoOfSessionsOnActivateUser() {
        return NO_OF_SESSIONS_ON_ACTIVATE_USER;
    }

    private long BLOCK_GENERATION_TIME;
    private long PIN_MAX_RETRY_COUNT;
    private String PRICE_POINT_CURRENCY_SYMBOL;
    private long REQUEST_TIMEOUT_DURATION;
    private long SESSION_BUFFER_TIME;
    private boolean USE_SEED_PASSWORD;
    private int NO_OF_SESSIONS_ON_ACTIVATE_USER;

    private OstConfigs(@NonNull final Context context, @Nullable JSONObject config) {
        if ( null == config ) {
            try {
                config = readConfig(context);
            } catch (Throwable th) {
                //ignore the config.
                Log.e(TAG, "Unable to read-config. The exception has been handled silently.");
                th.printStackTrace();
                config = new JSONObject();
            }
        }

        BLOCK_GENERATION_TIME = config.optLong("BLOCK_GENERATION_TIME", 3);
        if (BLOCK_GENERATION_TIME < 1) {
            throw new OstError("Ost_config_sc_bgt", ErrorCode.INVALID_BLOCK_GENERATION_TIME);
        }

        PIN_MAX_RETRY_COUNT = config.optLong("PIN_MAX_RETRY_COUNT", 3);
        if (PIN_MAX_RETRY_COUNT < 1) {
            throw new OstError("Ost_config_sc_pmrc", ErrorCode.INVALID_PIN_MAX_RETRY_COUNT);
        }

        PRICE_POINT_CURRENCY_SYMBOL = config.optString("PRICE_POINT_CURRENCY_SYMBOL", "USD");
        if (TextUtils.isEmpty(PRICE_POINT_CURRENCY_SYMBOL)) {
            throw new OstError("Ost_config_sc_ppcs", ErrorCode.INVALID_PRICE_POINT_CURRENCY_SYMBOL);
        }


        REQUEST_TIMEOUT_DURATION = config.optLong("REQUEST_TIMEOUT_DURATION", 60);
        if (REQUEST_TIMEOUT_DURATION < 1) {
            throw new OstError("Ost_config_sc_rtd", ErrorCode.INVALID_REQUEST_TIMEOUT_DURATION);
        }

        SESSION_BUFFER_TIME = config.optLong("SESSION_BUFFER_TIME", 3600);
        if (SESSION_BUFFER_TIME < 0) {
            throw new OstError("Ost_config_sc_sbt", ErrorCode.INVALID_SESSION_BUFFER_TIME);
        }

        USE_SEED_PASSWORD = config.optBoolean("USE_SEED_PASSWORD", false);

        NO_OF_SESSIONS_ON_ACTIVATE_USER = config.optInt("NO_OF_SESSIONS_ON_ACTIVATE_USER", 1);
        if (NO_OF_SESSIONS_ON_ACTIVATE_USER < 1 || NO_OF_SESSIONS_ON_ACTIVATE_USER > 5) {
            throw new OstError("Ost_config_sc_nosoau", ErrorCode.INVALID_NO_OF_SESSIONS_ON_ACTIVATE_USER);
        }
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