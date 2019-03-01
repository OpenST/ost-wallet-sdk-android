package ost.com.sampleostsdkapplication;

import android.os.Handler;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.network.OstHttpRequestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MappyApiClient {
    private static final String TAG = "MappyApiCLient";
    private final OstHttpRequestClient mOstHttpRequestClient;
    private final Handler mHandler;

    public MappyApiClient() {
        mHandler = new Handler();
        mOstHttpRequestClient = new OstHttpRequestClient(App.BASE_URL_MAPPY);
    }

    public void getUser(String userId, Callback callback) {
        Map<String, Object> map = new HashMap<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = mOstHttpRequestClient.get(String.format("users/%s/", userId), map);
                    runOnUI(callback, true, response);
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUI(callback, false, null);
                }
            }
        }).start();
    }

    public void createUser(String name, String mobileNumber, Callback callback) {
        createUser(name, mobileNumber, "", callback);
    }

    public void createUser(String name, String mobileNumber, String description, Callback callback) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constants.USER_NAME, name);
        map.put(Constants.MOBILE_NUMBER, mobileNumber);
        map.put(Constants.DESCRIPTION, description);
        map.put(Constants.CREATE_OST_USER, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = mOstHttpRequestClient.post("users", map);
                    String id = response.optString("_id", null);
                    if (null == id) {
                        Log.e(TAG, response.toString());
                        runOnUI(callback, false, null);
                    } else {
                        runOnUI(callback, true, response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUI(callback, false, null);
                }
            }
        }).start();
    }

    public void registerDevice(String userId, JSONObject jsonObject, Callback callback) {
        Map<String, Object> map = new HashMap<>();
        try {
            JSONObject deviceObject = jsonObject.getJSONObject(OstSdk.DEVICE);
            map.put("address", deviceObject.getString(OstDevice.ADDRESS));
            map.put("api_signer_address", deviceObject.getString(OstDevice.API_SIGNER_ADDRESS));
            map.put("device_name", deviceObject.getString(OstDevice.DEVICE_NAME));
            map.put("device_uuid", deviceObject.getString(OstDevice.DEVICE_UUID));
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUI(callback, false, new JSONObject());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = mOstHttpRequestClient.post(String.format("users/%s/devices/", userId), map);
                    runOnUI(callback, true, response);
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUI(callback, false, null);
                }
            }
        }).start();
    }

    public void getUserList(Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, Object> map = new HashMap<>();
                    JSONObject response = mOstHttpRequestClient.get("users", map);
                    runOnUI(callback, true, response);
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUI(callback, false, null);
                }
            }
        }).start();
    }

    public void loginUser(String name, String mobileNumber, Callback callback) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constants.USER_NAME, name);
        map.put(Constants.MOBILE_NUMBER, mobileNumber);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = mOstHttpRequestClient.post("users/validate/", map);
                    String id = response.optString("_id", null);
                    if (null == id) {
                        Log.e(TAG, response.toString());
                        runOnUI(callback, false, null);
                    } else {
                        runOnUI(callback, true, response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                    runOnUI(callback, false, null);
                }
            }
        }).start();
    }

    private void runOnUI(Callback callback, boolean success, JSONObject response) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(success, response);
            }
        });
    }


    public interface Callback {
        void onResponse(boolean success, JSONObject response);
    }
}