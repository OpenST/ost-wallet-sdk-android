package com.ost.ostsdk.database;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigSharedPreferences {
    private static final String SHARED_PREF_NAME = "ostsdkconfig";

    private static volatile ConfigSharedPreferences INSTANCE;
    private SharedPreferences mSharedPreferences;
    private JSONObject mConfigJson = new JSONObject();

    public static ConfigSharedPreferences init(final Context context) {
        if (INSTANCE == null) {
            synchronized (ConfigSharedPreferences.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConfigSharedPreferences(context);
                }
            }
        }
        return INSTANCE;
    }

    private ConfigSharedPreferences(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

    }

    public String getValue(String key) {
        String dataString = mSharedPreferences.getString(key, null);
        return dataString;
    }

    private void initConfigJSON(Context context) {
        InputStream raw = null;
        try {
            raw = context.getAssets().open("config.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(raw, "UTF8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            mConfigJson =  new JSONObject(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
