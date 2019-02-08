package ost.com.sampleostsdkapplication;

import android.os.Handler;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.network.OstHttpRequestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class MappyApiClient {

    private static final String BASE_URL = "http://10.0.2.2:4040/api/";
    private final OstHttpRequestClient mOstHttpRequestClient;
    private final Handler mHandler;

    MappyApiClient() {
        mHandler = new Handler();
        mOstHttpRequestClient = new OstHttpRequestClient(BASE_URL);
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
        Map<String, Object> map = new HashMap<>();
        map.put("username", name);
        map.put("mobile_number", mobileNumber);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = mOstHttpRequestClient.post("users", map);
                    runOnUI(callback, true, response);
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
            map.put("api_signer_address", deviceObject.getString(OstDevice.PERSONAL_SIGN_ADDRESS));
            map.put("device_name", deviceObject.getString(OstDevice.DEVICE_NAME));
            map.put("device_uuid", deviceObject.getString(OstDevice.DEVICE_UUID));
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUI(callback, false, null);
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