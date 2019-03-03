package com.ost.mobilesdk.network;

import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstDeviceManager;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;
import com.ost.mobilesdk.models.entities.OstRule;
import com.ost.mobilesdk.models.entities.OstSession;
import com.ost.mobilesdk.models.entities.OstToken;
import com.ost.mobilesdk.models.entities.OstTokenHolder;
import com.ost.mobilesdk.models.entities.OstTransaction;
import com.ost.mobilesdk.models.entities.OstUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OstApiHelper implements OstHttpRequestClient.ResponseParser {
    private static final String TAG = "OstApiHelper";

    @Override
    public void parse(JSONObject jsonObject) {
        updateWithApiResponse(jsonObject);
    }

    public void updateWithApiResponse(JSONObject jsonObject) {
        try {
            Log.d(TAG, String.format("Response: %s", jsonObject.toString()));
            if (!jsonObject.getBoolean(OstConstants.RESPONSE_SUCCESS)) {
                Log.e(TAG, "JSON response false");
                return;
            }
            JSONObject jsonData = jsonObject.getJSONObject(OstConstants.RESPONSE_DATA);

            if (jsonData.has(OstSdk.USER)) {
                OstUser.parse(jsonData.getJSONObject(OstSdk.USER));
            }
            if (jsonData.has(OstSdk.TRANSACTION)) {
                OstTransaction.parse(jsonData.getJSONObject(OstSdk.TRANSACTION));
            }
            if (jsonData.has(OstSdk.TOKEN_HOLDER)) {
                OstTokenHolder.parse(jsonData.getJSONObject(OstSdk.TOKEN_HOLDER));
            }
            if (jsonData.has(OstSdk.TOKEN)) {
                OstToken.parse(jsonData.getJSONObject(OstSdk.TOKEN));
            }
            if (jsonData.has(OstSdk.SESSION)) {
                OstSession.parse(jsonData.getJSONObject(OstSdk.SESSION));
            }
            if (jsonData.has(OstSdk.SESSIONS)) {
                JSONArray jsonArray = jsonData.getJSONArray(OstSdk.SESSIONS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    OstRule.parse(jsonArray.getJSONObject(i));
                }
            }
            if (jsonData.has(OstSdk.RULE)) {
                OstRule.parse(jsonData.getJSONObject(OstSdk.RULE));
            }
            if (jsonData.has(OstSdk.RULES)) {
                JSONArray jsonArray = jsonData.getJSONArray(OstSdk.RULES);
                for (int i = 0; i < jsonArray.length(); i++) {
                    OstRule.parse(jsonArray.getJSONObject(i));
                }
            }
            if (jsonData.has(OstSdk.DEVICE_OPERATION)) {
                OstDeviceManagerOperation.parse(jsonData.getJSONObject(OstSdk.DEVICE_OPERATION));
            }
            if (jsonData.has(OstSdk.DEVICE_MANAGER)) {
                OstDeviceManager.parse(jsonData.getJSONObject(OstSdk.DEVICE_MANAGER));
            }
            if (jsonData.has(OstSdk.DEVICE)) {
                OstDevice.parse(jsonData.getJSONObject(OstSdk.DEVICE));
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException");
        }
    }
}