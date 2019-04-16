/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ost.walletsdk.database.OstSdkDatabase;
import com.ost.walletsdk.database.OstSdkKeyDatabase;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.models.Impls.OstModelFactory;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstToken;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.utils.QRCode;
import com.ost.walletsdk.workflows.OstAbortDeviceRecovery;
import com.ost.walletsdk.workflows.OstActivateUser;
import com.ost.walletsdk.workflows.OstAddCurrentDeviceWithMnemonics;
import com.ost.walletsdk.workflows.OstAddSession;
import com.ost.walletsdk.workflows.OstExecuteTransaction;
import com.ost.walletsdk.workflows.OstGetPaperWallet;
import com.ost.walletsdk.workflows.OstLogoutAllSessions;
import com.ost.walletsdk.workflows.OstPerform;
import com.ost.walletsdk.workflows.OstRecoverDeviceWorkflow;
import com.ost.walletsdk.workflows.OstRegisterDevice;
import com.ost.walletsdk.workflows.OstResetPin;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is Single point to initiate all the OST Platform Work flows.
 * Before using this class initialize it first using method {@link #initialize(Context, String)}
 */
public class OstSdk {

    private static final String TAG = "OstSdk";

    // region - entity types
    public static final String USER = "user";
    public static final String TRANSACTION = "transaction";
    public static final String TOKEN_HOLDER = "token_holder";
    public static final String TOKEN = "token";
    public static final String SESSION = "session";
    public static final String RULE = "rule";
    public static final String DEVICE_OPERATION = "device_manager_operation";
    public static final String DEVICE_MANAGER = "device_manager";
    public static final String DEVICE = "device";
    public static final String RECOVERY_OWNER = "recovery_owner";
    // endregion


    // region - entity array types
    public static final String RULES = "rules";
    public static final String DEVICES = "devices";
    public static final String SESSIONS = "sessions";
    // endregion


    /**
     * Type of verify data context entity for execute rule transaction
     * In case of Direct Transfer
     * {
     * rule_name: "Direct Transfer",
     * token_holder_addresses: ["0xadd58909f6ee94cce3c5e816dba983bbadfa6fc4",
     * "0xadd23414f6ee94cce3c5e816dba983bbadfa6fc5"],
     * amounts: [2, 3],
     * token_id: 1055
     * }
     */
    public static final String JSON_OBJECT = "JSON";

    /**
     * Type of context entity which is byte[] for work flow
     * {@link #getDeviceMnemonics(String, OstWorkFlowCallback)}
     */
    public static final String MNEMONICS = "mnemonics";

    /**
     * Rule name to pass for workflow execute transactions
     *
     * @see OstSdk#executeTransaction(String, List, List, String ruleName, OstWorkFlowCallback)
     */
    public static final String RULE_NAME_DIRECT_TRANSFER = "direct transfer";
    public static final String RULE_NAME_PRICER = "pricer";

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
        BASE_URL = validateSdkUrl(baseUrl);
    }

    public static OstSdk get() {
        if (null == INSTANCE) {
            throw new RuntimeException("OstSdk.initialize() should be call before get");
        }
        return INSTANCE;
    }

    /**
     * Call this method once you application launches.
     * To perform config initialization and to run migrations if any.
     *
     * @param context Application context
     * @param baseUrl base Url of OST Platform
     */
    public static void initialize(Context context, String baseUrl) {
        if (INSTANCE == null) {
            synchronized (OstSdk.class) {
                if (INSTANCE == null) {
                    //Create Config.
                    OstConfigs.init(context);
                    //Create instance.
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

    // region - Work flows

    /**
     * Starts the workflow to activate the user.
     * User needs to be activated in order to transfer tokens.
     * This is the step where shall be setting their pin for the first time.
     * During this step User's token-holder is deployed on block-chain.
     * A session is also created. Sessions are needed to send tokens.
     * <p>
     * Note: Information contained in UserPassphrase shall be wiped out after use by Sdk.
     * Do not retain it. It can not be used more than once.
     *
     * @param passphrase         - A simple struct to transport pin information via app and Sdk.
     * @param expiresAfterInSecs - Time after which default user session should expire.
     * @param spendingLimit - The maximum amount of Tokens user can transfer in 1 transaction using the default session key.
     * @param callback           - A workflow callback handler.
     */
    public static void activateUser(UserPassphrase passphrase,
                                    long expiresAfterInSecs,
                                    String spendingLimit,
                                    OstWorkFlowCallback callback) {
        final OstActivateUser ostActivateUser = new OstActivateUser(passphrase, expiresAfterInSecs, spendingLimit, callback);
        ostActivateUser.perform();
    }

    private static void registerDevice(String userId, String tokenId, boolean forceSync, OstWorkFlowCallback callback) {
        final OstRegisterDevice ostRegisterDevice = new OstRegisterDevice(userId, tokenId, forceSync, callback);
        ostRegisterDevice.perform();
    }

    /**
     * To ensures that the current device is registered before communicating with OST Platform server.
     * Call this method every time app launches.
     *
     * @param userId           user Id
     * @param tokenId          token Id
     * @param workFlowCallback A workflow callback handler.
     */
    public static void setupDevice(String userId,
                                   String tokenId,
                                   OstWorkFlowCallback workFlowCallback) {
        registerDevice(userId, tokenId, false, workFlowCallback);
    }


    /**
     * To ensures that the current device is registered before communicating with OST Platform server.
     * Call this method every time app launches.
     *
     * @param userId           user Id
     * @param tokenId          token Id
     * @param forceSync        pass true if force sync of all the entities is needed.
     * @param workFlowCallback A workflow callback handler.
     */
    public static void setupDevice(String userId,
                                   String tokenId,
                                   boolean forceSync,
                                   OstWorkFlowCallback workFlowCallback) {
        registerDevice(userId, tokenId, forceSync, workFlowCallback);
    }

    /**
     * To perform workflow operations by reading QR data.
     * Method expects string retrieved from QR scan to be passed in data parameter.
     * performQRAction can perform Execute Rule Transactions, Add Device and Revoke Device.
     *
     * @param userId           user Id
     * @param data             data string retrieved  from QR scan
     * @param workFlowCallback A workflow callback handler
     * @throws JSONException Exception while parsing data string into JSON object
     */
    public static void performQRAction(String userId,
                                       String data,
                                       OstWorkFlowCallback workFlowCallback) throws JSONException {
        Log.i(TAG, String.format("Scanned text: %s", data));
        JSONObject payload = new JSONObject(data);
        final OstPerform ostPerform = new OstPerform(userId, payload, workFlowCallback);
        ostPerform.perform();
    }

    /**
     * To do any rule execution transaction sessions needs to be added.
     * Session is added to the user's current device with provided spec
     * like spendingLimit and expiry time in secs.
     * Session added are specific to device and can't be used from another device.
     *
     * @param userId             user id
     * @param spendingLimit spending limit of session
     * @param expireAfterInSecs  time delta in sec from current time
     * @param workFlowCallback   A workflow callback handler
     */
    public static void addSession(String userId,
                                  String spendingLimit,
                                  long expireAfterInSecs,
                                  OstWorkFlowCallback workFlowCallback) {
        final OstAddSession ostAddSession = new OstAddSession(userId, spendingLimit, expireAfterInSecs, workFlowCallback);
        ostAddSession.perform();
    }

    /**
     * It return 12 words mnemonics of the current device key in flowComplete callback.
     * In callback OstContextActivity should be used to get mnemonics as byte array.
     *
     * @param userId           user Id
     * @param workFlowCallback A workflow callback handler.
     */
    public static void getDeviceMnemonics(String userId, OstWorkFlowCallback workFlowCallback) {
        final OstGetPaperWallet ostGetPaperWallet = new OstGetPaperWallet(userId, workFlowCallback);
        ostGetPaperWallet.perform();
    }


    /**
     * For Documentation refer
     * {@link #executeTransaction(String, List, List, String, Map, OstWorkFlowCallback)}
     * Only difference is meta is passed as null
     */
    public static void executeTransaction(String userId,
                                          List<String> tokenHolderAddresses,
                                          List<String> amounts,
                                          String ruleName,
                                          OstWorkFlowCallback workFlowCallback) {
        executeTransaction(userId,
                tokenHolderAddresses,
                amounts,
                ruleName,
                new HashMap<>(),
                workFlowCallback);
    }


    /**
     * Start the workflow to execute rule transaction.
     * Before execute transactions make sure you have created Session having sufficient spending limit
     * You can create session by
     * {@link #addSession(String, String, long, OstWorkFlowCallback)} every time you need sessions and
     * {@link #activateUser(UserPassphrase, long, String, OstWorkFlowCallback)} once you activate user.
     * Rule name is need to be passed to execute rule.
     * {@link #RULE_NAME_DIRECT_TRANSFER#RULE_NAME_PRICER}
     * It can do multiple transfers by passing list of token holder receiver addresses with
     * respective amounts.
     *
     * @param userId               user Id of transaction addressee
     * @param tokenHolderAddresses List<String> token holder addresses list to where amounts need to be sent
     * @param amounts              List<String> amounts list corresponding to token holder addresses
     * @param ruleName             rule name to execute in transaction
     * @param meta                 data about transaction example:-
     *                             {name: "transaction name",
     *                             type "user-to-user",
     *                             details, "like"}
     * @param workFlowCallback     workflow callback handler.
     */
    public static void executeTransaction(String userId,
                                          List<String> tokenHolderAddresses,
                                          List<String> amounts,
                                          String ruleName,
                                          Map<String, Object> meta,
                                          OstWorkFlowCallback workFlowCallback) {
        final OstExecuteTransaction ostExecuteTransaction = new OstExecuteTransaction(userId,
                tokenHolderAddresses,
                amounts,
                ruleName,
                meta,
                workFlowCallback);

        ostExecuteTransaction.perform();
    }


    /**
     * Authorizes current device using mnemonics (12 words) of already authorized device.
     * IMPORTANT: The provided byte[] of mnemonics will be replaced with random bytes after user.
     *
     * @param userId              - userId for current user provided by OST Platform.
     * @param mnemonics           - UTF-8 encoded byte[] of mnemonics of the authorized device. The device must belong to the user.
     * @param ostWorkFlowCallback - Workflow callback interact.
     */
    public static void authorizeCurrentDeviceWithMnemonics(String userId,
                                                           byte[] mnemonics,
                                                           OstWorkFlowCallback ostWorkFlowCallback) {
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
     * To change current passPhrase to new passPhrase
     *
     * @param userId            user Id whose passPhrase to change
     * @param currentPassphrase Struct of current passPhrase
     * @param newPassphrase     Struct of new passPhrase
     * @param workFlowCallback  Work flow interact
     */
    public static void resetPin(String userId,
                                UserPassphrase currentPassphrase,
                                UserPassphrase newPassphrase,
                                OstWorkFlowCallback workFlowCallback) {
        final OstResetPin ostResetPin = new OstResetPin(userId,
                currentPassphrase,
                newPassphrase,
                workFlowCallback);
        ostResetPin.perform();
    }

    /**
     * It will authorize the current device by revoking provided device address.
     *
     * @param userId                 user id of recovery user
     * @param passphrase             Struct of current passPhrase
     * @param deviceAddressToRecover Address of device to recover
     * @param workFlowCallback       Work flow interact
     */
    public static void initiateDeviceRecovery(String userId,
                                              UserPassphrase passphrase,
                                              String deviceAddressToRecover,
                                              OstWorkFlowCallback workFlowCallback) {
        final OstRecoverDeviceWorkflow ostRecoverDeviceWorkflow = new OstRecoverDeviceWorkflow(userId,
                passphrase,
                deviceAddressToRecover,
                workFlowCallback);
        ostRecoverDeviceWorkflow.perform();
    }

    /**
     * If there are any on-going initiate recovery in process, It will abort that recovery process
     *
     * @param userId           userId of recovery user
     * @param passphrase       A simple struct to transport pin information via app and Sdk.
     * @param workFlowCallback Workflow callback Interact
     */
    public static void abortDeviceRecovery(String userId,
                                           UserPassphrase passphrase,
                                           OstWorkFlowCallback workFlowCallback) {
        final OstAbortDeviceRecovery ostAbortDeviceRecovery = new OstAbortDeviceRecovery(userId,
                passphrase,
                workFlowCallback);
        ostAbortDeviceRecovery.perform();
    }

    /**
     * It will revoke all the sessions associated with provided userId
     *
     * @param userId           user Id whose sessions to revoke
     * @param workFlowCallback Workflow callback interact.
     */
    public static void logoutAllSessions(String userId,
                                         OstWorkFlowCallback workFlowCallback) {
        final OstLogoutAllSessions ostLogoutAllSessions = new OstLogoutAllSessions(userId, workFlowCallback);
        ostLogoutAllSessions.perform();
    }
    // endregion

    private String validateSdkUrl(String baseUrl) {
        try {
            new URL(baseUrl);
        } catch (MalformedURLException e) {
            throw new OstError("cu_vsu_1", OstErrors.ErrorCode.INVALID_SDK_URL);
        }

        baseUrl = baseUrl.trim();
        if ('/' == baseUrl.charAt(baseUrl.length() - 1)) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        String[] endPointSplits = baseUrl.split("/");
        if (endPointSplits.length < 5) {
            throw new OstError("cu_vsu_2", OstErrors.ErrorCode.INVALID_SDK_URL);
        }
        String providedApiVersion = endPointSplits[4].toLowerCase();
        String expectedApiVersions = String.format("v%s", OstConstants.OST_API_VERSION).toLowerCase();
        if ( !providedApiVersion.equalsIgnoreCase(expectedApiVersions) ) {
            throw new OstError("cu_vsu_3", OstErrors.ErrorCode.INVALID_SDK_URL);
        }
        return baseUrl;
    }
}