package com.ost.ostsdk.network;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.ostsdk.OstSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URLEncoder;

@RunWith(AndroidJUnit4.class)
public class KitApiTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext);
        OstSdk.setCurrentUserId("6c6ea645-d86d-4449-8efa-3b54743f83de");
    }

    @Test
    public void testGetApiCall() {
        // Context of the app under test.
        /*  Create handle for the RetrofitInstance interface*/
        try {
            JSONObject jsonObject = new OstApiClient().getToken();
            boolean success = jsonObject.optBoolean("success");
            Assert.assertTrue(success);
        } catch (IOException e) {
            Assert.fail("Exception");
        }
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