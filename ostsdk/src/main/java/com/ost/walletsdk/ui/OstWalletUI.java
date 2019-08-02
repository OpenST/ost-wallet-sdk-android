package com.ost.walletsdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ui.recovery.RecoveryFragment;
import com.ost.walletsdk.ui.sdkInteract.SdkInteract;
import com.ost.walletsdk.ui.sdkInteract.WorkFlowListener;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.theme.ThemeConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class OstWalletUI {

    public static String activateUser(@NonNull Activity currentActivity, String userId, long expiredAfterSecs,
                                                String spendingLimit, OstUserPassphraseCallback userPassphraseCallback) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        workFlowListener.setUserPassPhraseCallback(userPassphraseCallback);
        Intent intent = new Intent(currentActivity, OstWorkFlowActivity.class);
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
        Intent intent = new Intent(currentActivity, OstWorkFlowActivity.class);
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
        Intent intent = new Intent(currentActivity, OstWorkFlowActivity.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.ABORT_RECOVERY);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        currentActivity.startActivity(intent);
        return workFlowListener.getId();
    }

    public static void initialize(Context context, String url) {
        OstSdk.initialize(context, url);
        try {
            ThemeConfig.init(context, new JSONObject("{\n" +
                    "\n" +
                    "  \"nav_bar_logo_image\": {\n" +
                    "    \"asset_name\": \"dummy_logo\"\n" +
                    "  },\n" +
                    "\n" +
                    "\"h1\": {\n" +
                    "  \"size\": 20,\n" +
                    "  \"font\": \"SFProDisplay\",\n" +
                    "  \"color\": \"#438bad\",\n" +
                    "  \"font_style\": \"semi_bold\"\n" +
                    "},\n" +
                    "\n" +
                    "\"h2\": {\n" +
                    "  \"size\": 17,\n" +
                    "  \"font\": \"SFProDisplay\",\n" +
                    "  \"color\": \"#666666\",\n" +
                    "  \"font_style\": \"medium\"\n" +
                    "},\n" +
                    "\n" +
                    "\"h3\": {\"size\": 15,\n" +
                    "  \"color\": \"#888888\",\n" +
                    "  \"font_style\": \"regular\"\n" +
                    "},\n" +
                    "\n" +
                    "\"h4\": {\"size\": 12,\n" +
                    "  \"color\": \"#888888\",\n" +
                    "  \"font_style\": \"regular\"\n" +
                    "},\n" +
                    "\n" +
                    "\"c1\": {\"size\": 14,\n" +
                    "  \"font\": \"SFProDisplay\",\n" +
                    "  \"color\": \"#484848\",\n" +
                    "  \"font_style\": \"bold\"\n" +
                    "},\n" +
                    "\n" +
                    "\"c2\": {\"size\": 12,\n" +
                    "  \"font\": \"SFProDisplay\",\n" +
                    "  \"color\": \"#6F6F6F\",\n" +
                    "  \"font_style\": \"regular\"\n" +
                    "},\n" +
                    "\n" +
                    "\"b1\": {\n" +
                    "  \"size\": 17,\n" +
                    "  \"color\": \"#ffffff\",\n" +
                    "  \"background_color\": \"#438bad\",\n" +
                    "  \"font_style\": \"medium\"\n" +
                    "},\n" +
                    "\n" +
                    "\"b2\": {\n" +
                    "  \"size\": 17,\n" +
                    "  \"color\": \"#438bad\",\n" +
                    "  \"background_color\": \"#ffffff\",\n" +
                    "  \"font_style\": \"semi_bold\"\n" +
                    "},\n" +
                    "\n" +
                    "\"b3\": {\n" +
                    "  \"size\": 12,\n" +
                    "  \"color\": \"#ffffff\",\n" +
                    "  \"background_color\": \"#438bad\",\n" +
                    "  \"font_style\": \"medium\"\n" +
                    "},\n" +
                    "\n" +
                    "\"b4\": {\n" +
                    "  \"size\": 12,\n" +
                    "  \"color\": \"#438bad\",\n" +
                    "  \"background_color\": \"#ffffff\",\n" +
                    "  \"font_style\": \"medium\"\n" +
                    "}\n" +
                    "}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            ContentConfig.init(context, new JSONObject("{\n" +
                    "  \"activate_user\": {\n" +
                    "    \"create_pin\": {\n" +
                    "      \"terms_and_condition_url\": \"https://www.google.com\"\n" +
                    "    },\n" +
                    "    \"confirm_pin\": {\n" +
                    "      \"terms_and_condition_url\": \"https://view.ost.com\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"));
        } catch (JSONException e) {
            e.printStackTrace();
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
}
