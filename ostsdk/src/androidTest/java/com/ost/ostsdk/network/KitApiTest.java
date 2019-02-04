package com.ost.ostsdk.network;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.ost.ostsdk.OstSdk;

import org.junit.BeforeClass;
import org.junit.Test;

public class KitApiTest {
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext);
    }

    @Test
    public void testApiCall() {
        // Context of the app under test.
        /*  Create handle for the RetrofitInstance interface*/
//        KitApi service = ApiClient.getClient().create(KitApi.class);
//
//        /*  Call the method with parameter in the interface to get the notice data*/
//        Call<ResponseBody> call = service.getNoticeData();
//
//        /* Log the URL called*/
//        Log.i("URL Called", call.request().url() + "");
//
//        JSONObject json = ApiClient.syncApiCall(call);
//
//        boolean success = json.optBoolean("success");
//        Assert.assertTrue(success);
    }
}