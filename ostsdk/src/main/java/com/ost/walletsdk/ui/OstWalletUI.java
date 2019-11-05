package com.ost.walletsdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ui.interfaces.OstWalletUIListener;
import com.ost.walletsdk.ui.recovery.RecoveryFragment;
import com.ost.walletsdk.ui.sdkInteract.SdkInteract;
import com.ost.walletsdk.ui.sdkInteract.WorkFlowListener;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.theme.ThemeConfig;
import com.ost.walletsdk.ui.workflow.OstAbortRecoveryWorkflow;
import com.ost.walletsdk.ui.workflow.OstActivateWorkflow;
import com.ost.walletsdk.ui.workflow.OstAuthorizeDeviceMnemonics;
import com.ost.walletsdk.ui.workflow.OstAuthorizeDeviceViaQRWorkflow;
import com.ost.walletsdk.ui.workflow.OstBiometricPrefWorkflow;
import com.ost.walletsdk.ui.workflow.OstCreateSessionWorkflow;
import com.ost.walletsdk.ui.workflow.OstExecuteTxnViaQRWorkflow;
import com.ost.walletsdk.ui.workflow.OstGetDeviceMnemonics;
import com.ost.walletsdk.ui.workflow.OstInitiateRecoveryWorkflow;
import com.ost.walletsdk.ui.workflow.OstResetPinWorkflow;
import com.ost.walletsdk.ui.workflow.OstRevokeDeviceWorkflow;
import com.ost.walletsdk.ui.workflow.OstShowDeviceQR;
import com.ost.walletsdk.ui.workflow.OstWorkFlowActivity;

import org.json.JSONObject;

public class OstWalletUI {

    /**
     * To initialize OstWallet before performing any workflow operations
     *
     * @param context Application context
     * @param url     Ost Platform url
     */
    public static void initialize(Context context, String url) {
        OstWalletUI.initialize(context, url, null);
    }

    /**
     * To initialize OstWallet before performing any workflow operations
     *
     * @param context Application context
     * @param url     Ost Platform url
     * @param config Application Config
     */
    public static void initialize(Context context, String url, @Nullable JSONObject config) {
        OstSdk.initialize(context, url, config);
        if (!ThemeConfig.isInitialized()) {
            setThemeConfig(context, null);
        }
        if (!ContentConfig.isInitialized()) {
            setContentConfig(context, null);
        }
    }

    /**
     * To Set Theme Configuration of components
     *
     * @param context     Application context
     * @param themeConfig ThemeConfig JSONObject
     */
    public static void setThemeConfig(Context context, JSONObject themeConfig) {
        if (null == themeConfig) themeConfig = new JSONObject();
        ThemeConfig.init(context, themeConfig);
    }

    /**
     * To set Content Configuration of Wallet UI Workflow
     *
     * @param context       Application context
     * @param contentConfig ContentConfig JSONObject
     */
    public static void setContentConfig(Context context, JSONObject contentConfig) {
        if (null == contentConfig) contentConfig = new JSONObject();
        ContentConfig.init(context, contentConfig);
    }

    /**
     * User activation refers to the deployment of smart-contracts that form the user's Brand Token wallet.
     * An activated user can engage with a Brand Token economy.
     *
     * @param currentActivity        Context of current activity of the application from which workflow will initiate
     * @param userId                 OST Platform user id provided by application server
     * @param expiredAfterSecs       Session key valid duration
     * @param spendingLimit          Spending limit in a transaction in atto BT
     * @param userPassphraseCallback Callback implementation object to get passphrase prefix from application
     * @return workflow Id
     */
    public static String activateUser(@NonNull Activity currentActivity, String userId, long expiredAfterSecs,
                                      String spendingLimit, OstUserPassphraseCallback userPassphraseCallback) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        workFlowListener.setUserPassPhraseCallback(userPassphraseCallback);
        Intent intent = new Intent(currentActivity, OstActivateWorkflow.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.ACTIVATE_USER);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        intent.putExtra(OstWorkFlowActivity.EXPIRED_AFTER_SECS, expiredAfterSecs);
        intent.putExtra(OstWorkFlowActivity.SPENDING_LIMIT, spendingLimit);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }

    /**
     * A user can control their Brand Tokens using their authorized devices. If they lose their authorized device,
     * they can recover access to their BrandTokens by authorizing a new device via the recovery process .
     *
     * @param currentActivity        Context of current activity of the application from which workflow will initiate
     * @param userId                 OST Platform user id provided by application server
     * @param address                Device address which wants to recover
     * @param userPassphraseCallback Callback implementation object to get passphrase prefix from application
     * @return workflow Id
     */
    public static String initiateDeviceRecovery(@NonNull Activity currentActivity, String userId,
                                                @Nullable String address, OstUserPassphraseCallback userPassphraseCallback) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        workFlowListener.setUserPassPhraseCallback(userPassphraseCallback);
        Intent intent = new Intent(currentActivity, OstInitiateRecoveryWorkflow.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.INITIATE_RECOVERY);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        intent.putExtra(RecoveryFragment.DEVICE_ADDRESS, address);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }

    /**
     * To abort initiated device recovery.
     *
     * @param currentActivity        Context of current activity of the application from which workflow will initiate
     * @param userId                 OST Platform user id provided by application server
     * @param userPassphraseCallback Callback implementation object to get passphrase prefix from application
     * @return workflow Id
     */
    public static String abortDeviceRecovery(@NonNull Activity currentActivity, String userId,
                                             OstUserPassphraseCallback userPassphraseCallback) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        workFlowListener.setUserPassPhraseCallback(userPassphraseCallback);
        Intent intent = new Intent(currentActivity, OstAbortRecoveryWorkflow.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.ABORT_RECOVERY);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }

    /**
     * A session is a period of time during which a sessionKey is authorized to sign transactions under a pre-set limit on behalf of the user.
     * The device manager, which controls the tokens, authorizes sessions.
     *
     * @param currentActivity        Context of current activity of the application from which workflow will initiate
     * @param userId                 OST Platform user id provided by application server
     * @param expiredAfterSecs       Session key valid duration
     * @param spendingLimit          Spending limit in a transaction in atto BT
     * @param userPassphraseCallback Callback implementation object to get passphrase prefix from application
     * @return workflow Id
     */
    public static String createSession(@NonNull Activity currentActivity, String userId, long expiredAfterSecs, String spendingLimit, OstUserPassphraseCallback userPassphraseCallback) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        workFlowListener.setUserPassPhraseCallback(userPassphraseCallback);
        Intent intent = new Intent(currentActivity, OstCreateSessionWorkflow.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.CREATE_SESSION);
        intent.putExtra(OstWorkFlowActivity.EXPIRED_AFTER_SECS, expiredAfterSecs);
        intent.putExtra(OstWorkFlowActivity.SPENDING_LIMIT, spendingLimit);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }

    /**
     * The user's PIN is set when activating the user.
     * This method supports re-setting a PIN and re-creating the recoveryOwner as part of that.
     *
     * @param currentActivity        Context of current activity of the application from which workflow will initiate
     * @param userId                 OST Platform user id provided by application server
     * @param userPassphraseCallback Callback implementation object to get passphrase prefix from application
     * @return workflow Id
     */
    public static String resetPin(@NonNull Activity currentActivity, String userId, OstUserPassphraseCallback userPassphraseCallback) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        workFlowListener.setUserPassPhraseCallback(userPassphraseCallback);
        Intent intent = new Intent(currentActivity, OstResetPinWorkflow.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.RESET_PIN);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }

    /**
     * The mnemonic phrase represents a human-readable way to authorize a new device. This phrase is 12 words long.
     *
     * @param currentActivity        Context of current activity of the application from which workflow will initiate
     * @param userId                 OST Platform user id provided by application server
     * @param userPassphraseCallback Callback implementation object to get passphrase prefix from application
     * @return workflow Id
     */
    public static String getDeviceMnemonics(@NonNull Activity currentActivity, String userId, OstUserPassphraseCallback userPassphraseCallback) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        workFlowListener.setUserPassPhraseCallback(userPassphraseCallback);
        Intent intent = new Intent(currentActivity, OstGetDeviceMnemonics.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.GET_DEVICE_MNEMONICS);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }

    /**
     * To revoke device address
     *
     * @param currentActivity        Context of current activity of the application from which workflow will initiate
     * @param userId                 OST Platform user id provided by application server
     * @param address                Device address to revoke
     * @param userPassphraseCallback Callback implementation object to get passphrase prefix from application
     * @return workflow Id
     */
    public static String revokeDevice(@NonNull Activity currentActivity, String userId,
                                      @Nullable String address, OstUserPassphraseCallback userPassphraseCallback) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        workFlowListener.setUserPassPhraseCallback(userPassphraseCallback);
        Intent intent = new Intent(currentActivity, OstRevokeDeviceWorkflow.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.INITIATE_RECOVERY);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        intent.putExtra(RecoveryFragment.DEVICE_ADDRESS, address);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }

    /**
     * To authorize device with mnemonics passphrase
     *
     * @param currentActivity        Context of current activity of the application from which workflow will initiate
     * @param userId                 OST Platform user id provided by application server
     * @param userPassphraseCallback Callback implementation object to get passphrase prefix from application
     * @return workflow Id
     */
    public static String authorizeCurrentDeviceWithMnemonics(@NonNull Activity currentActivity, String userId,
                                                             OstUserPassphraseCallback userPassphraseCallback) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        workFlowListener.setUserPassPhraseCallback(userPassphraseCallback);
        Intent intent = new Intent(currentActivity, OstAuthorizeDeviceMnemonics.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.AUTHORIZE_DEVICE_WITH_MNEMONICS);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }

    /**
     * This method can be used to enable or disable the biometric.
     *
     * @param currentActivity        Context for current Activity for the application
     * @param userId                 - user Id
     * @param enable                 - to enable or disable
     * @param userPassphraseCallback - A workflow callback handler.
     * @return workflow Id
     */
    public static String updateBiometricPreference(@NonNull Activity currentActivity, String userId,
                                                   boolean enable, OstUserPassphraseCallback userPassphraseCallback) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        workFlowListener.setUserPassPhraseCallback(userPassphraseCallback);
        Intent intent = new Intent(currentActivity, OstBiometricPrefWorkflow.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.UPDATE_BIOMETRIC_PREFERENCE);
        intent.putExtra(OstWorkFlowActivity.ENABLE, enable);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }

    /**
     * This method provides QR code to authorize current device from authorized device
     *
     * @param currentActivity - Context for current Activity for the application
     * @param userId          - OST Platform user id provided by application server
     * @return workflow Id
     */
    public static String getAddDeviceQRCode(@NonNull Activity currentActivity, String userId) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        Intent intent = new Intent(currentActivity, OstShowDeviceQR.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.SHOW_QR);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }

    /**
     * Authorize device by scanning QR code.
     * @param currentActivity        Context for current Activity for the application
     * @param userId                 - user Id
     * @param userPassphraseCallback - A workflow callback handler.
     * @return workflow Id
     */
    public static String scanQRCodeToAuthorizeDevice(@NonNull Activity currentActivity, String userId,
                                                     OstUserPassphraseCallback userPassphraseCallback) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        workFlowListener.setUserPassPhraseCallback(userPassphraseCallback);
        Intent intent = new Intent(currentActivity, OstAuthorizeDeviceViaQRWorkflow.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.AUTHORIZE_DEVICE_VIA_QR);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }


    /**
     * Execute Transaction by scanning the QR
     * @param currentActivity        Context for current Activity for the application
     * @param userId                 - user Id
     * @param userPassphraseCallback - A workflow callback handler.
     * @return workflow Id
     */
    public static String scanQRCodeToExecuteTransaction(@NonNull Activity currentActivity, String userId,
                                                        OstUserPassphraseCallback userPassphraseCallback) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        workFlowListener.setUserPassPhraseCallback(userPassphraseCallback);
        Intent intent = new Intent(currentActivity, OstExecuteTxnViaQRWorkflow.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.AUTHORIZE_TXN_VIA_QR);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }

    /**
     * Subscribe for any particular Workflow callback
     *
     * @param workflowId id of workflow to subscribe to.
     * @param listener   OstWalletUIListener object that implements respective workflow callback
     */
    public static void subscribe(String workflowId, OstWalletUIListener listener) {
        SdkInteract.getInstance().subscribe(workflowId, listener);
    }

    /**
     * Unsubscribe the listener.
     *
     * @param workflowId id of workflow to subscribe to.
     * @param listener   wallet listener
     */
    public static void unsubscribe(String workflowId, OstWalletUIListener listener) {
        SdkInteract.getInstance().unsubscribe(workflowId, listener);
    }

    /**
     * Component sheet is collection of all components present in OstWalletUI.
     * Developers can verify how components are going to look with provied theme.
     *
     * @param currentActivity Context for current Activity for the application
     */
    public static void showComponentSheet(@NonNull Activity currentActivity) {
        Intent intent = new Intent(currentActivity, OstWorkFlowActivity.class);
        currentActivity.startActivity(intent);
    }
}
