package com.ost.mobilesdk.database;

import android.arch.persistence.room.TypeConverter;

import org.json.JSONObject;

public class Converters {
    @TypeConverter
    public static JSONObject fromString(String string) {
        try {
            return new JSONObject(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @TypeConverter
    public static String jsonToString(JSONObject jsonObject) {
        return jsonObject.toString();
    }
}