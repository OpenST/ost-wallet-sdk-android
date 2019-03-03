package com.ost.mobilesdk.network;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstToken;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.security.OstApiSigner;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Http api client over
 *
 * @see OstHttpRequestClient
 * specific for Kit calls
 */
public class OstApiClient {
    private static final String TAG = "OstApiClient";

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
    private final OstHttpRequestClient.ResponseParser mResponseParser;
    private final OstApiSigner mApiSigner;

    public OstHttpRequestClient.ResponseParser getResponseParser() {
        return mResponseParser;
    }

    public OstApiClient(String userId, String baseUrl) {
        mUserId = userId;
        mOstUser = OstSdk.getUser(userId);

        mOstHttpRequestClient = new OstHttpRequestClient(baseUrl);
        mApiSigner = new OstApiSigner(mUserId);
        mOstHttpRequestClient.setOstApiSigner(mApiSigner);

        mResponseParser = new OstApiHelper();
        mOstHttpRequestClient.setResponseParser(mResponseParser);
    }

    public OstApiClient(String userId) {
        this(userId, OstSdk.get().get_BASE_URL());
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

    public JSONObject postUserActivate(Map<String, Object> map) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/activate-user/", mUserId), requestMap);
    }

    public JSONObject getDevice(String address) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/devices/%s/", mUserId, address), requestMap);
    }

    public JSONObject getUser() throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s", mUserId), requestMap);
    }

    private Map<String, Object> getPrerequisiteMap() {
        Map<String, Object> map = new HashMap<>();

        // api token_id.user_id.device_address.personal_sign_address
        map.put(API_KEY, String.format("%s.%s.%s.%s", mOstUser.getTokenId(), mUserId,
                mOstUser.getCurrentDevice().getAddress(),
                mOstUser.getCurrentDevice().getApiSignerAddress()));
        map.put(API_REQUEST_TIMESTAMP, String.valueOf((int) (System.currentTimeMillis() / 1000)));
        map.put(API_SIGNATURE_KIND, SIG_TYPE);
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

    public JSONObject postAddDevice(Map<String, Object> map) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/devices/authorize", mUserId), requestMap);
    }

    public JSONObject postAddSession(Map<String, Object> map) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/sessions/authorize", mUserId), requestMap);
    }

    public JSONObject getSession(String address) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/sessions/%s", mUserId, address), requestMap);
    }

    public JSONObject getDeviceManager() throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/device-managers", mUserId), requestMap);
    }

    public JSONObject postExecuteTransaction(Map<String, Object> map) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/transactions", mUserId), requestMap);
    }

    public JSONObject getTransaction(String transactionId) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/transactions/%s", mUserId, transactionId), requestMap);
    }

    public JSONObject getAllTransactions() throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/transactions", mUserId), requestMap);
    }

    public JSONObject getAllRules() throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get("/rules", requestMap);
    }

    public JSONObject postRecoveryOwners(Map<String, Object> map) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/recovery-owners", mUserId), requestMap);
    }

    public JSONObject getRecoveryOwnerAddress(String recoveryAddress) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get(String.format("/users/%s/recovery-owners/%s", mUserId, recoveryAddress), requestMap);
    }

    public JSONObject postRevokeDevice(Map<String, Object> map) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/devices/revoke", mUserId), requestMap);
    }

    public JSONObject postInitiateRecovery(Map<String, Object> map) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/devices/initiate-recovery", mUserId), requestMap);
    }

    public JSONObject postAbortRecovery(Map<String, Object> map) throws IOException {
        Map<String, Object> requestMap = getPrerequisiteMap();
        requestMap.putAll(map);
        return mOstHttpRequestClient.post(String.format("/users/%s/devices/abort-recovery", mUserId), requestMap);
    }
}