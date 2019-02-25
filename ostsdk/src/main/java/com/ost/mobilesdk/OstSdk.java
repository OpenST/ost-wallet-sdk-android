package com.ost.mobilesdk;

import android.content.Context;
import android.util.Log;

import com.ost.mobilesdk.database.ConfigSharedPreferences;
import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.OstSdkKeyDatabase;
import com.ost.mobilesdk.models.Impls.OstModelFactory;
import com.ost.mobilesdk.models.entities.OstToken;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.utils.CommonUtils;
import com.ost.mobilesdk.workflows.OstActivateUser;
import com.ost.mobilesdk.workflows.OstAddDevice;
import com.ost.mobilesdk.workflows.OstAddSession;
import com.ost.mobilesdk.workflows.OstExecuteTransaction;
import com.ost.mobilesdk.workflows.OstGetPaperWallet;
import com.ost.mobilesdk.workflows.OstPerform;
import com.ost.mobilesdk.workflows.OstRegisterDevice;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class OstSdk {
    public static final String RULES = "rules";
    public static final String USER = "user";
    public static final String TRANSACTION = "transaction";
    public static final String TOKEN_HOLDER = "token_holder";
    public static final String TOKEN = "token";
    public static final String SESSION = "session";
    public static final String RULE = "rule";
    public static final String DEVICE_OPERATION = "device_manager_operation";
    public static final String DEVICE_MANAGER = "device_manager";
    public static final String DEVICE = "device";
    public static final String SESSIONS = "sessions";
    private static final String TAG = "OstSdk";
    private static volatile OstSdk INSTANCE;

    private static Context mApplicationContext;
    private final String BASE_URL;

    public static Context getContext() {
        return mApplicationContext;
    }

    private OstSdk(Context context, String baseUrl) {
        mApplicationContext = context.getApplicationContext();
        OstSdkDatabase.initDatabase(mApplicationContext);
        OstSdkKeyDatabase.initDatabase(mApplicationContext);
        ConfigSharedPreferences.init(mApplicationContext);
        BASE_URL = baseUrl;
    }

    public static OstSdk get() {
        if (null == INSTANCE) {
            throw new RuntimeException("OstSdk.init() should be call before get");
        }
        return INSTANCE;
    }

    public static void init(Context context, String baseUrl) {
        if (INSTANCE == null) {
            synchronized (OstSdk.class) {
                if (INSTANCE == null) {
                    INSTANCE = new OstSdk(context, baseUrl);
                }
            }
        }
    }

    public String get_BASE_URL() {
        return BASE_URL;
    }

    public static OstToken getToken(String tokenId) {
        return OstModelFactory.getTokenModel().getEntityById(tokenId);
    }

    public static OstUser getUser(String id) {
        return OstModelFactory.getUserModel().getEntityById(id);
    }

    public static void activateUser(String userId, String uPin, String password, long expiresAfterInSecs, String spendingLimitInWei, OstWorkFlowCallback callback) {
        final OstActivateUser ostActivateUser = new OstActivateUser(userId, uPin, password, expiresAfterInSecs, spendingLimitInWei, callback);
        ostActivateUser.perform();
    }

    public static void registerDevice(String userId, String tokenId, boolean forceSync, OstWorkFlowCallback callback) {
        final OstRegisterDevice ostRegisterDevice = new OstRegisterDevice(userId, tokenId, forceSync, callback);
        ostRegisterDevice.perform();
    }

    public static void setupDevice(String userId, String tokenId, OstWorkFlowCallback workFlowCallback) {
        registerDevice(userId, tokenId, false, workFlowCallback);
    }

    public static void setupDevice(String userId, String tokenId, boolean forceSync, OstWorkFlowCallback workFlowCallback) {
        registerDevice(userId, tokenId, forceSync, workFlowCallback);
    }

    public static void addDevice(String userId, OstWorkFlowCallback workFlowCallback) {
        final OstAddDevice ostAddDevice = new OstAddDevice(userId, workFlowCallback);
        ostAddDevice.perform();
    }

    public static void scanQRCode(String userId, String data, OstWorkFlowCallback workFlowCallback) throws JSONException {
        Log.i(TAG, String.format("Scanned text: %s", data));
        JSONObject payload = new JSONObject(data);
        if (payload.has(OstConstants.DATA_DEFINATION)) {
            if (TRANSACTION.equals(payload.getString(OstConstants.DATA_DEFINATION))) {
                String tokenId = payload.getString(OstConstants.TOKEN_ID);
                String ruleName = payload.getString(OstConstants.RULE_NAME);
                JSONObject ruleParametersObj = payload.getJSONObject(OstConstants.RULE_PARAMETERS);

                JSONArray amountsObj = ruleParametersObj.getJSONArray(OstConstants.AMOUNTS);
                List<String> amounts = new CommonUtils().jsonArrayToList(amountsObj);

                JSONArray addressesObj = ruleParametersObj.getJSONArray(OstConstants.ADDRESSES);
                List<String> addresses = new CommonUtils().jsonArrayToList(addressesObj);

                executeTransaction(userId, tokenId ,addresses, amounts, ruleName, workFlowCallback);
            }
        } else {
            final OstPerform ostPerform = new OstPerform(userId, payload, workFlowCallback);
            ostPerform.perform();
        }
    }

    public static void addSession(String userId, String spendingLimit, long expireAfterInSecs, OstWorkFlowCallback workFlowCallback) {
        final OstAddSession ostAddSession = new OstAddSession(userId, spendingLimit, expireAfterInSecs, workFlowCallback);
        ostAddSession.perform();
    }

    public static void getPaperWallet(String userId, OstWorkFlowCallback workFlowCallback) {
        final OstGetPaperWallet ostGetPaperWallet = new OstGetPaperWallet(userId, workFlowCallback);
        ostGetPaperWallet.perform();
    }

    public static void executeTransaction(String userId, String tokenId, List<String> tokenHolderAddresses, List<String> amounts, String transactionType, OstWorkFlowCallback workFlowCallback) {
        final OstExecuteTransaction ostExecuteTransaction = new OstExecuteTransaction(userId, tokenId ,tokenHolderAddresses, amounts, transactionType, workFlowCallback);
        ostExecuteTransaction.perform();
    }
}