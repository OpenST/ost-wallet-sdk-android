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
import com.ost.walletsdk.ui.workflow.OstAbortRecoveryWorkflow;
import com.ost.walletsdk.ui.workflow.OstActivateWorkflow;
import com.ost.walletsdk.ui.workflow.OstCreateSessionWorkflow;
import com.ost.walletsdk.ui.workflow.OstInitiateRecoveryWorkflow;
import com.ost.walletsdk.ui.workflow.OstWorkFlowActivity;

import org.json.JSONException;
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

    public static void initialize(Context context, String url) {
        OstSdk.initialize(context, url);
        try {
            ThemeConfig.init(context, new JSONObject("{\n" +
                    "  \"nav_bar_logo_image\": {\n" +
                    "    \"asset_name\": \"ost_nav_bar_logo\"\n" +
                    "  },\n" +
                    "  \"icons\": {\n" +
                    "      \"cross\": {\n" +
                    "        \"tint_color\": \"#438bad\"\n" +
                    "      },\n" +
                    "      \"back\": {\n" +
                    "        \"tint_color\": \"#438bad\"\n" +
                    "      }\n" +
                    "  },\n" +
                    "  \"navigation_bar\": {\n" +
                    "    \"tint_color\": \"#ffffff\"\n" +
                    "  },\n" +
                    "  \"h1\": {\n" +
                    "    \"size\": 20,\n" +
                    "    \"color\": \"#438bad\",\n" +
                    "    \"font\": \"Lato-Bold\",\n" +
                    "    \"font_weight\": \"semi_bold\"\n" +
                    "  },\n" +
                    "  \"h2\": {\n" +
                    "    \"size\": 17,\n" +
                    "    \"color\": \"#666666\",\n" +
                    "    \"font\": \"Lato-Bold\",\n" +
                    "    \"font_weight\": \"medium\"\n" +
                    "  },\n" +
                    "  \"h3\": {\n" +
                    "    \"size\": 15,\n" +
                    "    \"color\": \"#888888\",\n" +
                    "    \"font\": \"Lato-Bold\",\n" +
                    "    \"font_weight\": \"regular\"\n" +
                    "  },\n" +
                    "  \"h4\": {\n" +
                    "    \"size\": 12,\n" +
                    "    \"color\": \"#888888\",\n" +
                    "    \"font\": \"Lato-Bold\",\n" +
                    "    \"font_weight\": \"regular\"\n" +
                    "  },\n" +
                    "  \"c1\": {\n" +
                    "    \"size\": 13,\n" +
                    "    \"color\": \"#484848\",\n" +
                    "    \"font\": \"Lato-Bold\",\n" +
                    "    \"font_weight\": \"bold\",\n" +
                    "    \"alignment\": \"left\"\n" +
                    "  },\n" +
                    "  \"c2\": {\n" +
                    "    \"size\": 12,\n" +
                    "    \"color\": \"#6f6f6f\",\n" +
                    "    \"font_weight\": \"regular\",\n" +
                    "    \"font\": \"Lato-Bold\",\n" +
                    "    \"alignment\": \"left\"\n" +
                    "  },\n" +
                    "  \"b1\": {\n" +
                    "    \"size\": 17,\n" +
                    "    \"color\": \"#ffffff\",\n" +
                    "    \"background_color\": \"#438bad\",\n" +
                    "    \"font\": \"Lato-Bold\",\n" +
                    "    \"font_weight\": \"medium\"\n" +
                    "  },\n" +
                    "  \"b2\": {\n" +
                    "    \"size\": 17,\n" +
                    "    \"color\": \"#438bad\",\n" +
                    "    \"background_color\": \"#ffffff\",\n" +
                    "    \"font\": \"Lato-Bold\",\n" +
                    "    \"font_weight\": \"semi_bold\"\n" +
                    "  },\n" +
                    "  \"b3\": {\n" +
                    "    \"size\": 12,\n" +
                    "    \"color\": \"#ffffff\",\n" +
                    "    \"background_color\": \"#438bad\",\n" +
                    "    \"font\": \"Lato-Bold\",\n" +
                    "    \"font_weight\": \"medium\"\n" +
                    "  },\n" +
                    "  \"b4\": {\n" +
                    "    \"size\": 12,\n" +
                    "    \"color\": \"#438bad\",\n" +
                    "    \"background_color\": \"#ffffff\",\n" +
                    "    \"font\": \"Lato-Bold\",\n" +
                    "    \"font_weight\": \"medium\"\n" +
                    "  },\n" +
                    "  \"fonts\": {\n" +
                    "    \"Lato-Bold\": \"fonts/Lato-Italic.ttf\"\n" +
                    "  }\n" +
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
