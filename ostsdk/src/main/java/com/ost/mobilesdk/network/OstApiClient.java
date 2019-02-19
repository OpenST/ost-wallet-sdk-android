package com.ost.mobilesdk.network;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstToken;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.security.OstApiSigner;
import com.ost.mobilesdk.security.OstKeyManager;

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

    private static final String BASE_URL = "https://s5-api.stagingost.com/testnet/v2";
    private static final String API_KEY = "api_key";
    private static final String API_REQUEST_TIMESTAMP = "api_request_timestamp";
    private static final String API_SIGNATURE_KIND = "api_signature_kind";
    private static final String TOKEN_ID = "token_id";
    private static final String USER_ID = "user_id";
    private static final String SIG_TYPE = "OST1-PS";
    private static final String SESSION_ADDRESSES = "session_addresses";
    private static final String EXPIRATION_HEIGHT = "expiration_height";
    private static final String SPENDING_LIMIT = "spending_limit";
    private static final String RECOVERY_OWNER_ADDRESS = "recovery_owner_address";
    private static final String DEVICE_ADDRESS = "device_address";
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
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get("/tokens/", requestMap);
    }

    public JSONObject postUserActivate(String sessionAddress, String expirationHeight, String spendingLimit, String recoveryOwnerAddress) throws IOException {
        Map<String, Object> requestMap = new HashMap<>();

        requestMap.put(SESSION_ADDRESSES, Arrays.asList(sessionAddress));
        requestMap.put(EXPIRATION_HEIGHT, expirationHeight);
        requestMap.put(SPENDING_LIMIT, spendingLimit);
        requestMap.put(RECOVERY_OWNER_ADDRESS, recoveryOwnerAddress);
        requestMap.put(DEVICE_ADDRESS, mOstUser.getCurrentDevice().getAddress());
        return postUserActivate(requestMap);
    }

    public JSONObject postUserActivate(Map<String,Object> map) throws IOException {
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

        // api key as “deviceAddress.apiSignerAddress”
        map.put(API_KEY, String.format("%s.%s.%s.%s",mOstUser.getTokenId(), mUserId, mOstUser.getCurrentDevice().getAddress(),
                mOstUser.getCurrentDevice().getPersonalSignAddress()));
        map.put(API_REQUEST_TIMESTAMP, String.valueOf((int)(System.currentTimeMillis()/1000)));
        map.put(API_SIGNATURE_KIND, SIG_TYPE);
        map.put(TOKEN_ID, mOstUser.getTokenId());
        map.put(USER_ID, mUserId);
        return map;
    }

    public JSONObject getSalt() throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/salts", mUserId), requestMap);
    }

    public JSONObject getCurrentBlockNumber() throws IOException {
        String tokenId = mOstUser.getTokenId();
        OstToken ostToken = OstToken.getById(tokenId);
        String chainId = ostToken.getChainId();
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/chains/%s", chainId), requestMap);
    }

    public JSONObject postAddDevice(Map<String,Object> map) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/devices/authorize", mUserId), requestMap);
    }
}