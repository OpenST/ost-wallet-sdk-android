package com.ost.mobilesdk.network;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.security.OstApiSigner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;

@RunWith(AndroidJUnit4.class)
public class KitApiTest {
    private static final String TAG = "KitApiTest";
    private static final String USER_ID = "6c6ea645-d86d-4449-8efa-3b54743f83de";
    private static final String TOKEN_ID = "58";
    private static Context mAppContext;

    @BeforeClass
    public static void setUp() {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext);
    }


    private static MockOstUser createUser(String userId, String tokenId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OstUser.ID, userId);
            jsonObject.put(OstUser.TOKEN_ID, tokenId);
            jsonObject.put(OstUser.TOKEN_HOLDER_ADDRESS, "");
            jsonObject.put(OstUser.DEVICE_MANAGER_ADDRESS, "");
            jsonObject.put(OstUser.TYPE, "");
            return  new MockOstUser(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class MockOstUser extends OstUser {

        public MockOstUser(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
            super(id, parentId, data, status, updatedTimestamp);
        }

        public MockOstUser(JSONObject jsonObject) throws JSONException {
            super(jsonObject);
        }

        @Override
        public OstDevice getCurrentDevice() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(OstDevice.ADDRESS, "0x60A20Cdf6a21a73Fb89475221D252865C695e302");
                jsonObject.put(OstDevice.PERSONAL_SIGN_ADDRESS, "0xf65c7a49981db56AED34beA4617E32e326ACf977");
                jsonObject.put(OstDevice.USER_ID, USER_ID);
                jsonObject.put(OstDevice.DEVICE_NAME, "deviceName");
                jsonObject.put(OstDevice.DEVICE_UUID, "uuid");
                jsonObject.put(OstDevice.DEVICE_MANAGER_ADDRESS, "");
                jsonObject.put(OstDevice.UPDATED_TIMESTAMP, System.currentTimeMillis());
                jsonObject.put(OstDevice.STATUS, OstDevice.CONST_STATUS.CREATED);
                Log.i(TAG, String.format("Ost Device JSON Object %s", jsonObject.toString()));
                return new OstDevice(jsonObject);
            } catch (JSONException e) {
                Log.i(TAG, String.format("Exception in OstDevice creation %s", e.getMessage()));
                e.printStackTrace();
            }
            return null;
        }
    }

    @Test
    public void testGetApiCall() {
        // Context of the app under test.
        /*  Create handle for the RetrofitInstance interface*/
        try {
            OstApiClient ostApiClient = new OstApiClient(USER_ID);
            OstApiSigner ostApiSigner = new OstApiSigner(Numeric.hexStringToByteArray("0x6edc3804eb9f70b26731447b4e43955c5532f2195a6fe77cbed287dbd3c762ce"));
            ostApiClient.getOstHttpRequestClient().setOstApiSigner(ostApiSigner);
            try {
                Field field = ostApiClient.getClass().getDeclaredField("mOstUser");
                field.setAccessible(true);
                field.set(ostApiClient, createUser(USER_ID, TOKEN_ID));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = ostApiClient.getToken();
            boolean success = jsonObject.optBoolean("success");
            Assert.assertTrue(success);
        } catch (IOException e) {
            Assert.fail("Exception");
        }
    }

    @Test
    public void testPostTokenApiCall() {
        // Context of the app under test.
        /*  Create handle for the RetrofitInstance interface*/
        try {
            OstApiClient ostApiClient = new OstApiClient(USER_ID);
            OstApiSigner ostApiSigner = new OstApiSigner(Numeric.hexStringToByteArray("0x6edc3804eb9f70b26731447b4e43955c5532f2195a6fe77cbed287dbd3c762ce"));
            ostApiClient.getOstHttpRequestClient().setOstApiSigner(ostApiSigner);
            try {
                Field field = ostApiClient.getClass().getDeclaredField("mOstUser");
                field.setAccessible(true);
                field.set(ostApiClient, createUser(USER_ID, TOKEN_ID));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = ostApiClient.postTokenDeployment("0x60A20Cdf6a21a73Fb89475221D252865C695e302","1","1");
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
        jsonObject.put("a", "1");
        JSONObject subJsonObject = new JSONObject();
        subJsonObject.put("f", "1");
        subJsonObject.put("e", "4");
        jsonObject.put("m", subJsonObject);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("d");
        jsonArray.put("c");
        jsonObject.put("b", jsonArray);

        String jsonString = jsonObject.toString();

        String encoded = URLEncoder.encode(jsonString);

        Assert.assertNotNull(encoded);
    }
}