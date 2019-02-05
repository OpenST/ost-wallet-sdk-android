package com.ost.ostsdk.network;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.ost.ostsdk.OstSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

@RunWith(AndroidJUnit4.class)
public class KitApiTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext);
        OstSdk.setUserInfo("bcffc567-2881-4610-aa86-fa89f37bc197", "58");
    }

    @Test
    public void testGetApiCall() {
        // Context of the app under test.
        /*  Create handle for the RetrofitInstance interface*/
        KitApi service = ApiClient.getClient().create(KitApi.class);

        /*  Call the method with parameter in the interface to get the notice data*/
        Map<String,String> queryParamsMap = new HashMap<>();
        Call<ResponseBody> call = service.getTokens();

        /* Log the URL called*/
        Log.i("URL Called", call.request().url() + "");

        JSONObject json = ApiClient.syncApiCall(call);

        boolean success = json.optBoolean("success");
        Assert.assertTrue(success);
    }

    @Test
    public void testPostApiCall() throws JSONException {
        // Context of the app under test.
        /*  Create handle for the RetrofitInstance interface*/
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("a","1");
        JSONObject subJsonObject = new JSONObject();
        subJsonObject.put("f","1");
        subJsonObject.put("e","4");
        jsonObject.put("m", subJsonObject);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("d");
        jsonArray.put("c");
        jsonObject.put("b",jsonArray);

        String jsonString = jsonObject.toString();

        String encoded = URLEncoder.encode(jsonString);

        Assert.assertNotNull(encoded);
    }
}