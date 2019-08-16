package com.ost.walletsdk.ui.workflow;

import android.os.Bundle;

import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.recovery.AbortRecoveryFragment;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import static com.ost.walletsdk.ui.recovery.RecoveryFragment.SHOW_BACK_BUTTON;

public class OstAbortRecoveryWorkflow extends OstWorkFlowActivity {


    @Override
    boolean invalidState() {
        if (super.invalidState()) return true;

        if (!OstUser.CONST_STATUS.ACTIVATED.equalsIgnoreCase(
                OstUser.getById(mUserId).getStatus()
        )) {
            mWorkFlowListener.flowInterrupt(
                    getWorkflowContext(),
                    new OstError("owfa_oc_ar_1", OstErrors.ErrorCode.USER_NOT_ACTIVATED)
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
        bundle.putBoolean(SHOW_BACK_BUTTON, false);
        FragmentUtils.addFragment(R.id.layout_container,
                AbortRecoveryFragment.newInstance(bundle),
                this);
    }

    @Override
    OstWorkflowContext getWorkflowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.ABORT_DEVICE_RECOVERY);
    }
}