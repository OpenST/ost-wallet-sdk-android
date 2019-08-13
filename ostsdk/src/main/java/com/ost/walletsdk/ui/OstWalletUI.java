package com.ost.walletsdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
import com.ost.walletsdk.ui.workflow.OstBiometricPrefWorkflow;
import com.ost.walletsdk.ui.workflow.OstCreateSessionWorkflow;
import com.ost.walletsdk.ui.workflow.OstGetDeviceMnemonics;
import com.ost.walletsdk.ui.workflow.OstInitiateRecoveryWorkflow;
import com.ost.walletsdk.ui.workflow.OstResetPinWorkflow;
import com.ost.walletsdk.ui.workflow.OstRevokeDeviceWorkflow;
import com.ost.walletsdk.ui.workflow.OstWorkFlowActivity;

import org.json.JSONObject;

public class OstWalletUI {

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

    public static String createSession(@NonNull Activity currentActivity, String userId, long expiryTime, String spendingLimit, OstUserPassphraseCallback userPassphraseCallback) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        workFlowListener.setUserPassPhraseCallback(userPassphraseCallback);
        Intent intent = new Intent(currentActivity, OstCreateSessionWorkflow.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.CREATE_SESSION);
        intent.putExtra(OstWorkFlowActivity.EXPIRED_AFTER_SECS, expiryTime);
        intent.putExtra(OstWorkFlowActivity.SPENDING_LIMIT, spendingLimit);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }

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
     * To update Biometric preference
     *
     * @param currentActivity context for current Activity for the application
     * @param userId - user Id
     * @param enable - to enable or disable
     * @param userPassphraseCallback  - A workflow callback handler.
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
     * Subscribe for any particular Workflow callback
     * @param workflowId id of workflow to subscribe to.
     * @param listener OstWalletUIListener object that implements respective workflow callback
     */
    public static void subscribe(String workflowId, OstWalletUIListener listener) {
        SdkInteract.getInstance().subscribe(workflowId, listener);
    }

    /**
     * Unsubscribe the listener.
     * @param workflowId id of workflow to subscribe to.
     * @param listener
     */
    public  static void unsubscribe(String workflowId, OstWalletUIListener listener) {
        SdkInteract.getInstance().unsubscribe(workflowId, listener);
    }

    public static void initialize(Context context, String url) {
        OstSdk.initialize(context, url);
        if ( !ThemeConfig.isInitialized() ) {
            setThemeConfig(context, null);
        }
        if ( !ContentConfig.isInitialized() ) {
            setContentConfig(context, null);
        }
    }

    public static void setThemeConfig(Context context, JSONObject themeConfig) {
        if (null == themeConfig) themeConfig = new JSONObject();
        ThemeConfig.init(context, themeConfig);
    }

    public static void setContentConfig(Context context, JSONObject contentConfig) {
        if (null == contentConfig) contentConfig = new JSONObject();
        ContentConfig.init(context, contentConfig);
    }

    public static void showComponentSheet(@NonNull Activity currentActivity) {
        Intent intent = new Intent(currentActivity, OstWorkFlowActivity.class);
        currentActivity.startActivity(intent);
    }
}
