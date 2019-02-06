package com.ost.ostsdk.network;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.entities.OstUser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OstApiClient {

    public static final String BASE_URL = "https://s4-api.stagingost.com/testnet/v2";

    private static final String API_SIGNER_ADDRESS = "api_signer_address";
    private static final String REQUEST_TIMESTAMP = "request_timestamp";
    private static final String SIGNATURE_KIND = "signature_kind";
    private static final String TOKEN_ID = "token_id";
    private static final String WALLET_ADDRESS = "wallet_address";
    private static final String USER_ID = "user_id";
    private static final String SIG_TYPE = "OST1-PS";
    private final OstHttpRequestClient mOstHttpRequestClient;
    private final String mUserId;
    private final OstUser mOstUser;

    public OstApiClient(String userId, String baseUrl) {
        mOstUser = OstSdk.getUser(userId);
        mUserId = userId;
        mOstHttpRequestClient = new OstHttpRequestClient(baseUrl);
    }

    public OstApiClient(String userId) {
        this(userId, BASE_URL);
    }

    public OstHttpRequestClient getOstHttpRequestClient() {
        return mOstHttpRequestClient;
    }

    public OstApiClient() {
        this(OstSdk.getCurrentUserId(), BASE_URL);
    }

    public JSONObject getToken() throws IOException {

        Map<String, Object> requestMap = getPrerequisiteMap();
        return mOstHttpRequestClient.get("/tokens/", requestMap);
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
}