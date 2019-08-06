package com.ost.walletsdk.ui.workflow;

import android.os.Bundle;

import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.resetpin.ResetPinFragment;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

public class OstResetPinWorkflow extends OstWorkFlowActivity {

    @Override
    boolean invalidState() {
        if (super.invalidState()) return true;

        if (!OstUser.CONST_STATUS.ACTIVATED.equalsIgnoreCase(
                OstUser.getById(mUserId).getStatus()
        )) {
            mWorkFlowListener.flowInterrupt(
                    new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.RESET_PIN),
                    new OstError("owfa_oc_rp_1", OstErrors.ErrorCode.USER_NOT_ACTIVATED)
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
        FragmentUtils.addFragment(R.id.layout_container,
                ResetPinFragment.newInstance(bundle),
                this);

    }
}