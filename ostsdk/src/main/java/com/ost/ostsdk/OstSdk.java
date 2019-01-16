package com.ost.ostsdk;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ost.ostsdk.Network.ApiClient;
import com.ost.ostsdk.Network.KitApi;
import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.OstSdkKeyDatabase;
import com.ost.ostsdk.models.Impls.ModelFactory;
import com.ost.ostsdk.models.TaskCallback;
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

    private static Context mApplicationContext;

    public static Context getContext() {
        return mApplicationContext;
    }

    public static void init(Context context) {
        mApplicationContext = context.getApplicationContext();
        OstSdkDatabase.initDatabase(mApplicationContext);
        OstSdkKeyDatabase.initDatabase(mApplicationContext);
    }

    public static OstToken registerToken(JSONObject jsonObject, @NonNull TaskCallback callback) throws JSONException {
        return ModelFactory.getTokenModel().registerToken(jsonObject, callback);
    }

    public static OstToken registerToken(JSONObject jsonObject) throws JSONException {
        return registerToken(jsonObject, new TaskCallback() {
        });
    }

    public static OstToken getToken(String tokenId) {
        return ModelFactory.getTokenModel().getTokenById(tokenId);
    }

    public static OstUser initUser(JSONObject jsonObject) throws JSONException {
        return initUser(jsonObject, new TaskCallback() {
        });
    }

    public static OstUser initUser(JSONObject jsonObject, @NonNull TaskCallback callback) throws JSONException {
        return ModelFactory.getUserModel().initUser(jsonObject, callback);
    }

    public static OstUser getUser(String id) {
        return ModelFactory.getUserModel().getUserById(id);
    }

    public static void delUser(String userId) {
        delUser(userId, new TaskCallback() {
        });
    }

    public static void delUser(String userId, @NonNull TaskCallback callback) {
        ModelFactory.getUserModel().deleteUser(userId, callback);
    }

    public static KitApi getKitNetworkClient() {
        return ApiClient.getClient().create(KitApi.class);
    }
}