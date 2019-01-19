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
}