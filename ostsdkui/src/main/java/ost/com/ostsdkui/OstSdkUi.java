package ost.com.ostsdkui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.ost.walletsdk.OstSdk;

import ost.com.ostsdkui.sdkInteract.SdkInteract;
import ost.com.ostsdkui.sdkInteract.WorkFlowListener;

public class OstSdkUi {

    public static WorkFlowListener activateUser(@NonNull Activity currentActivity, String userId ,long expiredAfterSecs,
                                    String spendingLimit) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        Intent intent = new Intent(currentActivity, OstWorkFlowActivity.class);
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_ID, workFlowListener.getId());
        intent.putExtra(OstWorkFlowActivity.WORKFLOW_NAME, OstWorkFlowActivity.ACTIVATE_USER);
        intent.putExtra(OstWorkFlowActivity.USER_ID, userId);
        intent.putExtra(OstWorkFlowActivity.EXPIRED_AFTER_SECS, expiredAfterSecs);
        intent.putExtra(OstWorkFlowActivity.SPENDING_LIMIT, spendingLimit);
        currentActivity.startActivity(intent);
        return workFlowListener;
    }

    public static void initialize(Context context, String url, SdkInteract.SdkHelperCallback sdkHelperCallback) {
        OstSdk.initialize(context, url);
        SdkInteract.getInstance().setSdkHelper(sdkHelperCallback);
    }
}
