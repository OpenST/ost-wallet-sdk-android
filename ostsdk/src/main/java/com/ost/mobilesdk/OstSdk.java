package com.ost.mobilesdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ost.mobilesdk.database.ConfigSharedPreferences;
import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.OstSdkKeyDatabase;
import com.ost.mobilesdk.models.Impls.OstModelFactory;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstToken;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.security.UserPassphrase;
import com.ost.mobilesdk.utils.QRCode;
import com.ost.mobilesdk.workflows.OstActivateUser;
import com.ost.mobilesdk.workflows.OstAddCurrentDeviceWithMnemonics;
import com.ost.mobilesdk.workflows.OstAddSession;
import com.ost.mobilesdk.workflows.OstExecuteTransaction;
import com.ost.mobilesdk.workflows.OstGetPaperWallet;
import com.ost.mobilesdk.workflows.OstPerform;
import com.ost.mobilesdk.workflows.OstRecoverDeviceWorkflow;
import com.ost.mobilesdk.workflows.OstRegisterDevice;
import com.ost.mobilesdk.workflows.OstResetPin;
import com.ost.mobilesdk.workflows.OstStartPolling;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

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
    public static final String RECOVERY_OWNER = "recovery_owner";
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

    /**
     * Starts the workflow to activate the user.
     * User needs to be activated in order to transfer tokens.
     * This is the step where shall be setting their pin for the first time.
     * During this step User's token-holder is deployed on block-chain.
     * A session is also created. Sessions are needed to send tokens.
     *
     * Note: Information contained in UserPassphrase shall be wiped out after use by Sdk.
     * Do not retain it. It can not be used more than once.
     *
     * @param passphrase - A simple struct to transport pin information via app and Sdk.
     * @param expiresAfterInSecs - Time after which default user session should expire.
     * @param spendingLimitInWei - The maximum amount of Tokens user can transfer in 1 transaction using the default session key.
     * @param callback - A workflow callback handler.
     *
     */
    public static void activateUser(UserPassphrase passphrase, long expiresAfterInSecs, String spendingLimitInWei, OstWorkFlowCallback callback) {
        final OstActivateUser ostActivateUser = new OstActivateUser(passphrase,expiresAfterInSecs,spendingLimitInWei,callback);
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

    public static void ostPerform(String userId, String data, OstWorkFlowCallback workFlowCallback) throws JSONException {
        Log.i(TAG, String.format("Scanned text: %s", data));
        JSONObject payload = new JSONObject(data);
        final OstPerform ostPerform = new OstPerform(userId, payload, workFlowCallback);
        ostPerform.perform();
    }

    public static void addSession(String userId, String spendingLimit, long expireAfterInSecs, OstWorkFlowCallback workFlowCallback) {
        final OstAddSession ostAddSession = new OstAddSession(userId, spendingLimit, expireAfterInSecs, workFlowCallback);
        ostAddSession.perform();
    }

    public static void getPaperWallet(String userId, OstWorkFlowCallback workFlowCallback) {
        final OstGetPaperWallet ostGetPaperWallet = new OstGetPaperWallet(userId, workFlowCallback);
        ostGetPaperWallet.perform();
    }

    public static void executeTransaction(String userId, List<String> tokenHolderAddresses, List<String> amounts, String transactionType, OstWorkFlowCallback workFlowCallback) {
        final OstExecuteTransaction ostExecuteTransaction = new OstExecuteTransaction(userId, tokenHolderAddresses, amounts, transactionType, workFlowCallback);
        ostExecuteTransaction.perform();
    }

    /**
     * Authorizes current device using mnemonics (12 words) of already authorized device.
     * IMPORTANT: The provided byte[] of mnemonics will be replaced with random bytes after user.
     *
     * @param userId - userId for current user provided by Kit.
     * @param mnemonics - UTF-8 encoded byte[] of mnemonics of the authorized device. The device must belong to the user.
     * @param ostWorkFlowCallback - Workflow callback interact.
     */
    public static void addDeviceUsingMnemonics(String userId, byte[] mnemonics, OstWorkFlowCallback ostWorkFlowCallback) {
        OstAddCurrentDeviceWithMnemonics ostAddCurrentDeviceWithMnemonics = new OstAddCurrentDeviceWithMnemonics(userId, mnemonics, ostWorkFlowCallback);
        ostAddCurrentDeviceWithMnemonics.perform();
    }

    /**
     * Generates QR code, providing data of device for the given user Id
     *
     * @param userId id whose device qr to be generated
     * @return Bitmap image type
     */
    public static Bitmap getAddDeviceQRCode(String userId) {
        OstUser ostUser = OstUser.getById(userId);
        if (null == ostUser) {
            Log.e(TAG, String.format("gadqc_1 %s", OstErrors.getMessage(OstErrors.ErrorCode.USER_NOT_FOUND)));
            return null;
        }

        OstDevice ostDevice = ostUser.getCurrentDevice();
        if (null == ostDevice) {
            Log.e(TAG, String.format("gadqc_2 %s", OstErrors.getMessage(OstErrors.ErrorCode.CURRENT_DEVICE_NOT_FOUND)));
            return null;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OstConstants.QR_DATA_DEFINITION, OstConstants.DATA_DEFINITION_AUTHORIZE_DEVICE);
            jsonObject.put(OstConstants.QR_DATA_DEFINITION_VERSION, "1.0");

            JSONObject dataObject = new JSONObject();
            dataObject.put(OstConstants.QR_DEVICE_ADDRESS, ostDevice.getAddress());

            jsonObject.put(OstConstants.QR_DATA, dataObject);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected exception in createPayload");
            return null;
        }

        return QRCode.newInstance(OstSdk.getContext())
                .setContent(jsonObject.toString())
                .setErrorCorrectionLevel(ErrorCorrectionLevel.M)
                .setMargin(2)
                .getQRCOde();
    }

    /**
     * To initiate polling for entity
     *
     * @param userId
     * @param entityId
     * @param entityType
     * @param fromStatus
     * @param toStatus
     * @param workFlowCallback
     */
    public static void startPolling(String userId, String entityId, String entityType,
                                    String fromStatus, String toStatus, OstWorkFlowCallback workFlowCallback) {
        final OstStartPolling ostStartPolling = new OstStartPolling(userId, entityId,
                entityType, fromStatus, toStatus, workFlowCallback);
        ostStartPolling.perform();
    }


    public static void resetRecoveryPassphrase(String userId, UserPassphrase currentPassphrase, UserPassphrase newPassphrase, OstWorkFlowCallback workFlowCallback) {
        final OstResetPin ostResetPin = new OstResetPin(userId, currentPassphrase, newPassphrase,workFlowCallback);
        ostResetPin.perform();
    }

    public static void initiateRecoverDevice(String userId, UserPassphrase passphrase, String deviceAddressToRecover, OstWorkFlowCallback workFlowCallback) {
        final OstRecoverDeviceWorkflow ostRecoverDeviceWorkflow = new OstRecoverDeviceWorkflow(userId,
                passphrase,
                deviceAddressToRecover,
                workFlowCallback
            );
        ostRecoverDeviceWorkflow.perform();
    }

    public static void revokeRecoverDevice(String userId, UserPassphrase passphrase, String deviceAddressToRecover, String deviceAddressToAuthorize, OstWorkFlowCallback workFlowCallback) {
        //TBD.
    }
}