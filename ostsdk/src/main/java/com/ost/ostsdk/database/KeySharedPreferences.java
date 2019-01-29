package com.ost.ostsdk.database;

import android.content.Context;
import android.content.SharedPreferences;

public class KeySharedPreferences {
    private static final String SHARED_PREF_NAME = "ostsdkkey";

    private static volatile SharedPreferences INSTANCE;

    public static SharedPreferences init(final Context context) {
        if (INSTANCE == null) {
            synchronized (KeySharedPreferences.class) {
                if (INSTANCE == null) {
                    INSTANCE = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                }
            }
        }
        return INSTANCE;
    }

    public static SharedPreferences getPref() {
        if (INSTANCE == null) {
            throw new RuntimeException("KeySharedPreferences not initialized");
        }
        return INSTANCE;
    }
}
