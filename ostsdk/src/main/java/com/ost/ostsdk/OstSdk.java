package com.ost.ostsdk;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ost.ostsdk.Network.ApiClient;
import com.ost.ostsdk.Network.KitApi;
import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.OstSdkKeyDatabase;
import com.ost.ostsdk.models.Impls.OstModelFactory;
import com.ost.ostsdk.models.OstTaskCallback;
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

    public static OstToken registerToken(JSONObject jsonObject) throws JSONException {
        return OstModelFactory.getTokenModel().registerToken(jsonObject);
    }

    public static OstToken getToken(String tokenId) {
        return OstModelFactory.getTokenModel().getTokenById(tokenId);
    }

    public static OstUser initUser(JSONObject jsonObject) throws JSONException {
        return OstModelFactory.getUserModel().initUser(jsonObject);
    }

    public static OstUser getUser(String id) {
        return OstModelFactory.getUserModel().getUserById(id);
    }

    public static void delUser(String userId) {
        OstModelFactory.getUserModel().deleteUser(userId);
    }

    public static KitApi getKitNetworkClient() {
        return ApiClient.getClient().create(KitApi.class);
    }
}