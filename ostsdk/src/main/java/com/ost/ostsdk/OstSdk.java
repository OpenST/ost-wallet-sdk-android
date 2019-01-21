package com.ost.ostsdk;

import android.content.Context;

import com.ost.ostsdk.Network.ApiClient;
import com.ost.ostsdk.Network.KitApi;
import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.OstSdkKeyDatabase;
import com.ost.ostsdk.models.Impls.OstModelFactory;
import com.ost.ostsdk.models.entities.OstToken;
import com.ost.ostsdk.models.entities.OstUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Work Flows
 * 1. Key Recovery (Work flow, key Rotation)
 * 2. Additional Key Provisioning (Work Flow)
 * 3. QR code scanning (Work Flow)
 */
public class OstSdk {

    private static final String USER = "user";
    private static final String TRANSACTION = "transaction";
    private static final String TOKEN_HOLDER = "token_holder";
    private static final String TOKEN = "token";
    private static final String SESSION = "session";
    private static final String RULE = "rule";
    private static final String DEVICE_OPERATION = "device_operation";
    private static final String DEVICE_MANAGER = "device_manager";
    private static final String DEVICE = "device";
    private static final String CREDITS = "credits";

    private static Context mApplicationContext;

    public static Context getContext() {
        return mApplicationContext;
    }

    public static void init(Context context) {
        mApplicationContext = context.getApplicationContext();
        OstSdkDatabase.initDatabase(mApplicationContext);
        OstSdkKeyDatabase.initDatabase(mApplicationContext);
    }

    public static OstToken registerToken(JSONObject jsonObject) throws JSONException {
        return OstToken.parse(jsonObject);
    }

    public static OstToken getToken(String tokenId) {
        return OstModelFactory.getTokenModel().getEntityById(tokenId);
    }

    public static OstUser initUser(String id) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(OstUser.ID, id);
        jsonObject.put(OstUser.NAME, "");
        jsonObject.put(OstUser.TOKEN_ID, "");
        jsonObject.put(OstUser.TOKEN_HOLDER_ADDRESS, "");
        jsonObject.put(OstUser.DEVICE_MANAGER_ADDRESS, "");
        jsonObject.put(OstUser.TYPE, "");
        return OstUser.parse(jsonObject);
    }

    public static OstUser getUser(String id) {
        return OstModelFactory.getUserModel().getEntityById(id);
    }

    public static void delUser(String userId) {
        OstModelFactory.getUserModel().deleteEntity(userId);
    }

    public static KitApi getKitNetworkClient() {
        return ApiClient.getClient().create(KitApi.class);
    }

    public static void parse(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(OstSdk.USER)) {
            OstUser.parse(jsonObject.getJSONObject(OstSdk.USER));
        }
        if (jsonObject.has(OstSdk.TRANSACTION)) {
            OstUser.parse(jsonObject.getJSONObject(OstSdk.TRANSACTION));
        }
        if (jsonObject.has(OstSdk.TOKEN_HOLDER)) {
            OstUser.parse(jsonObject.getJSONObject(OstSdk.TOKEN_HOLDER));
        }
        if (jsonObject.has(OstSdk.TOKEN)) {
            OstUser.parse(jsonObject.getJSONObject(OstSdk.TOKEN));
        }
        if (jsonObject.has(OstSdk.SESSION)) {
            OstUser.parse(jsonObject.getJSONObject(OstSdk.SESSION));
        }
        if (jsonObject.has(OstSdk.RULE)) {
            OstUser.parse(jsonObject.getJSONObject(OstSdk.RULE));
        }
        if (jsonObject.has(OstSdk.DEVICE_OPERATION)) {
            OstUser.parse(jsonObject.getJSONObject(OstSdk.DEVICE_OPERATION));
        }
        if (jsonObject.has(OstSdk.DEVICE_MANAGER)) {
            OstUser.parse(jsonObject.getJSONObject(OstSdk.DEVICE_MANAGER));
        }
        if (jsonObject.has(OstSdk.DEVICE)) {
            OstUser.parse(jsonObject.getJSONObject(OstSdk.DEVICE));
        }
        if (jsonObject.has(OstSdk.CREDITS)) {
            OstUser.parse(jsonObject.getJSONObject(OstSdk.CREDITS));
        }
    }
}