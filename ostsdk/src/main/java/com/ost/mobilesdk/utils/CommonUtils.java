package com.ost.mobilesdk.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.ost.mobilesdk.models.entities.OstDevice.TAG;

public class CommonUtils {
    public CommonUtils() {
    }

    public JSONArray listToJSONArray(List<String> list) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            jsonArray.put(list.get(i));
        }
        return jsonArray;
    }

    public List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            Log.e(TAG, "jsonArray to list exception", e);
        }
        return list;
    }
}