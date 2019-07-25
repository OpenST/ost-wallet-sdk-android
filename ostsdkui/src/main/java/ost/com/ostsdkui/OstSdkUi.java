package ost.com.ostsdkui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.ost.walletsdk.OstSdk;

import org.json.JSONObject;

import ost.com.ostsdkui.sdkInteract.SdkInteract;
import ost.com.ostsdkui.sdkInteract.WorkFlowListener;
import ost.com.ostsdkui.uicomponents.uiutils.content.ContentConfig;
import ost.com.ostsdkui.uicomponents.uiutils.theme.ThemeConfig;

public class OstSdkUi {

    public static WorkFlowListener activateUser(@NonNull Activity currentActivity, String userId, long expiredAfterSecs,
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
        return workFlowListener;
    }

    public static void initialize(Context context, String url) {
        OstSdk.initialize(context, url);
        ThemeConfig.init(context, new JSONObject());
        ContentConfig.init(context, new JSONObject());
    }

    public static void setThemeConfig(Context context, JSONObject themeConfig) {
        if (null == themeConfig) themeConfig = new JSONObject();
        ThemeConfig.init(context, themeConfig);
    }

    public static void setContentConfig(Context context, JSONObject contentConfig) {
        if (null == contentConfig) contentConfig = new JSONObject();
        ContentConfig.init(context, new JSONObject());
    }
}
