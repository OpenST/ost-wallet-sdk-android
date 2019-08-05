package com.ost.walletsdk.ui.workflow;

import android.os.Bundle;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import static com.ost.walletsdk.ui.recovery.RecoveryFragment.SHOW_BACK_BUTTON;

public class OstCreateSessionWorkflow extends OstWorkFlowActivity {

    @Override
    boolean invalidState() {
        if (super.invalidState()) return true;

        if (!OstDevice.CONST_STATUS.AUTHORIZED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            mWorkFlowListener.flowInterrupt(
                    new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.ADD_SESSION),
                    new OstError("owfa_oc_cs_2", OstErrors.ErrorCode.DEVICE_NOT_SETUP)
            );
            finish();
            return true;
        }

        return false;
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();
        Bundle bundle = getIntent().getExtras();
        long expiredAfterSecs = bundle.getLong(EXPIRED_AFTER_SECS, 100000);
        String spendingLimit = bundle.getString(SPENDING_LIMIT);
        bundle.putBoolean(SHOW_BACK_BUTTON, false);
        showProgress(true,"Adding Session");
        OstSdk.addSession(mUserId, spendingLimit, expiredAfterSecs, mWorkFlowListener);
    }
}