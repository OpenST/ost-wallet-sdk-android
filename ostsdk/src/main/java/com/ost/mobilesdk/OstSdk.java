package com.ost.mobilesdk;

import android.content.Context;
import android.os.Handler;

import com.ost.mobilesdk.database.ConfigSharedPreferences;
import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.OstSdkKeyDatabase;
import com.ost.mobilesdk.models.Impls.OstModelFactory;
import com.ost.mobilesdk.models.entities.OstCredits;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstDeviceManager;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;
import com.ost.mobilesdk.models.entities.OstRule;
import com.ost.mobilesdk.models.entities.OstSession;
import com.ost.mobilesdk.models.entities.OstToken;
import com.ost.mobilesdk.models.entities.OstTokenHolder;
import com.ost.mobilesdk.models.entities.OstTransaction;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.workflows.OstDeployTokenHolder;
import com.ost.mobilesdk.workflows.OstRegisterDevice;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class OstSdk {

    public static final String USER = "user";
    public static final String TRANSACTION = "transaction";
    public static final String TOKEN_HOLDER = "token_holder";
    public static final String TOKEN = "token";
    public static final String SESSION = "session";
    public static final String RULE = "rule";
    public static final String DEVICE_OPERATION = "device_manager_operation";
    public static final String DEVICE_MANAGER = "device_manager";
    public static final String DEVICE = "device";
    public static final String CREDITS = "credits";
    private static volatile OstSdk INSTANCE;

    private static Context mApplicationContext;

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

    public static OstUser initUser(String id, String mTokenId) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(OstUser.ID, id);
        jsonObject.put(OstUser.NAME, "");
        jsonObject.put(OstUser.TOKEN_ID, mTokenId);
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


    public static void deployTokenHolder(String userId, String uPin, String password, String expirationHeight, String spendingLimit, OstWorkFlowCallback callback) {
        Handler handler = new Handler();
        final OstDeployTokenHolder ostDeployTokenHolder = new OstDeployTokenHolder(userId, uPin, password, expirationHeight, spendingLimit, handler, callback);
        ostDeployTokenHolder.perform();
    }

    public static void registerDevice(String userId,String tokenId ,boolean forceSync ,OstWorkFlowCallback callback) {
        Handler handler = new Handler();
        final OstRegisterDevice ostRegisterDevice = new OstRegisterDevice(userId, tokenId ,forceSync ,handler, callback);
        ostRegisterDevice.perform();
    }

    public static void setupDevice(String userId, String tokenId, OstWorkFlowCallback workFlowCallback) {
        registerDevice(userId, tokenId ,false ,workFlowCallback);
    }

    public static void setupDevice(String userId, String tokenId ,boolean forceSync ,OstWorkFlowCallback workFlowCallback) {
        registerDevice(userId, tokenId ,forceSync ,workFlowCallback);
    }

    OstDeployTokenHolder QRCodeInput() {
        OstDeployTokenHolder ostDeployTokenHolder = null;
        return ostDeployTokenHolder;
    }

    public static void parse(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(OstSdk.USER)) {
            OstUser.parse(jsonObject.getJSONObject(OstSdk.USER));
        }
        if (jsonObject.has(OstSdk.TRANSACTION)) {
            OstTransaction.parse(jsonObject.getJSONObject(OstSdk.TRANSACTION));
        }
        if (jsonObject.has(OstSdk.TOKEN_HOLDER)) {
            OstTokenHolder.parse(jsonObject.getJSONObject(OstSdk.TOKEN_HOLDER));
        }
        if (jsonObject.has(OstSdk.TOKEN)) {
            OstToken.parse(jsonObject.getJSONObject(OstSdk.TOKEN));
        }
        if (jsonObject.has(OstSdk.SESSION)) {
            OstSession.parse(jsonObject.getJSONObject(OstSdk.SESSION));
        }
        if (jsonObject.has(OstSdk.RULE)) {
            OstRule.parse(jsonObject.getJSONObject(OstSdk.RULE));
        }
        if (jsonObject.has(OstSdk.DEVICE_OPERATION)) {
            OstDeviceManagerOperation.parse(jsonObject.getJSONObject(OstSdk.DEVICE_OPERATION));
        }
        if (jsonObject.has(OstSdk.DEVICE_MANAGER)) {
            OstDeviceManager.parse(jsonObject.getJSONObject(OstSdk.DEVICE_MANAGER));
        }
        if (jsonObject.has(OstSdk.DEVICE)) {
            OstDevice.parse(jsonObject.getJSONObject(OstSdk.DEVICE));
        }
        if (jsonObject.has(OstSdk.CREDITS)) {
            OstCredits.parse(jsonObject.getJSONObject(OstSdk.CREDITS));
        }
    }
}