package com.ost.mobilesdk.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.security.OstApiSigner;
import com.ost.mobilesdk.security.OstKeyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Http api client over
 * @see OstHttpRequestClient
 * specific for Kit calls
 */
public class OstApiClient {
    private static final String TAG = "OstApiClient";

    private static final String BASE_URL = "https://s4-api.stagingost.com/testnet/v2";
    private static final String API_SIGNER_ADDRESS = "api_signer_address";
    private static final String REQUEST_TIMESTAMP = "request_timestamp";
    private static final String SIGNATURE_KIND = "signature_kind";
    private static final String TOKEN_ID = "token_id";
    private static final String WALLET_ADDRESS = "wallet_address";
    private static final String USER_ID = "user_id";
    private static final String SIG_TYPE = "OST1-PS";
    private static final String DEVICE_ADDRESSES = "device_addresses";
    private static final String SESSION_ADDRESSES = "session_addresses";
    private static final String EXPIRATION_HEIGHT = "expiration_height";
    private static final String SPENDING_LIMIT = "spending_limit";
    private static final String RECOVERY_ADDRESS = "recovery_address";
    private final OstHttpRequestClient mOstHttpRequestClient;
    private final String mUserId;
    private final OstUser mOstUser;

    public OstApiClient(String userId, String baseUrl) {
        mUserId = userId;
        mOstUser = OstSdk.getUser(userId);

        mOstHttpRequestClient = new OstHttpRequestClient(baseUrl);
        OstApiSigner ostApiSigner = new OstKeyManager(userId).getApiSigner();
        mOstHttpRequestClient.setOstApiSigner(ostApiSigner);
    }

    public OstApiClient(String userId) {
        this(userId, BASE_URL);
    }

    public OstHttpRequestClient getOstHttpRequestClient() {
        return mOstHttpRequestClient;
    }

    public JSONObject getToken() throws IOException {
        if (!isNetworkAvailable()) {
            try {
                return new JSONObject("{error:'network not available'}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get("/tokens/", requestMap);
    }

    public JSONObject postTokenDeployment(String sessionAddress, String expirationHeight, String spendingLimit, String recoveryAddress) throws IOException {
        Map<String, Object> requestMap = new HashMap<>();

        requestMap.put(SESSION_ADDRESSES, Arrays.asList(sessionAddress));
        requestMap.put(EXPIRATION_HEIGHT, expirationHeight);
        requestMap.put(SPENDING_LIMIT, spendingLimit);
        requestMap.put(RECOVERY_ADDRESS, recoveryAddress);
        return postTokenDeployment(requestMap);
    }

    public JSONObject postTokenDeployment(Map<String,Object> map) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/activate-user/", mUserId), requestMap);
    }

    public JSONObject getDevices() throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/devices/%s/", mUserId, mOstUser.getCurrentDevice().getAddress()), requestMap);
    }

    public JSONObject getUser() throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s", mUserId), requestMap);
    }

    private Map<String, Object> getPrerequisiteMap() {
        Map<String, Object> map = new HashMap<>();

        map.put(API_SIGNER_ADDRESS, mOstUser.getCurrentDevice().getPersonalSignAddress());
        map.put(REQUEST_TIMESTAMP, String.valueOf((int)(System.currentTimeMillis()/1000)));
        map.put(SIGNATURE_KIND, SIG_TYPE);
        map.put(TOKEN_ID, mOstUser.getTokenId());
        map.put(USER_ID, mUserId);
        map.put(WALLET_ADDRESS, mOstUser.getCurrentDevice().getAddress());
        return map;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) OstSdk.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}