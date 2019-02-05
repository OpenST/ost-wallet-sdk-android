package com.ost.ostsdk;

import android.content.Context;
import android.os.Handler;

import com.ost.ostsdk.database.ConfigSharedPreferences;
import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.OstSdkKeyDatabase;
import com.ost.ostsdk.models.Impls.OstModelFactory;
import com.ost.ostsdk.models.entities.OstToken;
import com.ost.ostsdk.models.entities.OstUser;
import com.ost.ostsdk.network.KitApi;
import com.ost.ostsdk.workflows.OstDeployTokenHolder;
import com.ost.ostsdk.workflows.OstRegisterDevice;
import com.ost.ostsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class OstSdk {

    public static final String USER = "user";
    public static final String TRANSACTION = "transaction";
    public static final String TOKEN_HOLDER = "token_holder";
    public static final String TOKEN = "token";
    public static final String SESSION = "session";
    public static final String RULE = "rule";
    public static final String DEVICE_OPERATION = "device_operation";
    public static final String DEVICE_MANAGER = "device_manager";
    public static final String DEVICE = "device";
    public static final String CREDITS = "credits";
    private static volatile OstSdk INSTANCE;

    private static Context mApplicationContext;
    private static String mUserId;

    public static Context getContext() {
        return mApplicationContext;
    }

    public static void init(Context context) {
        if (INSTANCE == null) {
            synchronized (OstSdk.class) {
                if (INSTANCE == null) {
                    INSTANCE = new OstSdk(context);
                }
            }
        }
    }

    public static OstSdk get() {
        if (null == INSTANCE) {
            throw new RuntimeException("OstSdk.init() should be call before get");
        }
        return INSTANCE;
    }

    private OstSdk(Context context) {
        mApplicationContext = context.getApplicationContext();
        OstSdkDatabase.initDatabase(mApplicationContext);
        OstSdkKeyDatabase.initDatabase(mApplicationContext);
        ConfigSharedPreferences.init(mApplicationContext);
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
        return null;
    }

    public static void deployTokenHolder(String userId, String tokenId, String uPin, String password, boolean isBiometricNeeded, OstWorkFlowCallback callback) {
        Handler handler = new Handler();
        final OstDeployTokenHolder ostDeployTokenHolder = new OstDeployTokenHolder(userId, tokenId, uPin, password, isBiometricNeeded ,handler, callback);
        ostDeployTokenHolder.perform();
    }

    public static void registerDevice(String userId, OstWorkFlowCallback callback) {
        Handler handler = new Handler();
        final OstRegisterDevice ostRegisterDevice = new OstRegisterDevice(userId, handler, callback);
        ostRegisterDevice.perform();
    }

    public static String getCurrentUserId() {
        return mUserId;
    }

    public static void setCurrentUserId(String userId) {
        mUserId = userId;
    }

    public static OstUser getCurrentUser() {
        return getUser(getCurrentUserId());
    }

    OstDeployTokenHolder QRCodeInput() {
        OstDeployTokenHolder  ostDeployTokenHolder = null;
        return ostDeployTokenHolder;
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