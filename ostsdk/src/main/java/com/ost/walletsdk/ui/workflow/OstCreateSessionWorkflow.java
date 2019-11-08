package com.ost.walletsdk.ui.workflow;

import android.os.Bundle;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import org.json.JSONObject;

import static com.ost.walletsdk.ui.recovery.RecoveryFragment.SHOW_BACK_BUTTON;

public class OstCreateSessionWorkflow extends OstWorkFlowActivity {

    final JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("add_session");


    @Override
    void ensureValidState() {
        super.ensureValidState();

        if (!OstDevice.CONST_STATUS.AUTHORIZED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            throw new OstError("owfa_evs_cs_1", OstErrors.ErrorCode.DEVICE_UNAUTHORIZED);
        }
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();

        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("initial_loader")).getString());
        getWorkflowLoader().onInitLoader();

        Bundle bundle = getIntent().getExtras();
        long expiredAfterSecs = bundle.getLong(EXPIRED_AFTER_SECS, 100000);
        String spendingLimit = bundle.getString(SPENDING_LIMIT);
        bundle.putBoolean(SHOW_BACK_BUTTON, false);
        OstSdk.addSession(mUserId, spendingLimit, expiredAfterSecs, mWorkFlowListener);
    }

    @Override
    JSONObject getContentString(OstWorkflowContext ostWorkflowContext) {
        return ContentConfig.getInstance()
                .getStringConfig("add_session")
                .optJSONObject("get_pin");
    }

    @Override
    OstWorkflowContext getWorkflowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.ADD_SESSION);
    }

    @Override
    public void popTopFragment() {
        super.popTopFragment();

        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("loader")).getString());
        getWorkflowLoader().onPostAuthentication();
    }
}