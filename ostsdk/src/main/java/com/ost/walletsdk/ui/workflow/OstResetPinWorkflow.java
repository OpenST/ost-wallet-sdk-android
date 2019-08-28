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
    void ensureValidState() {
        super.ensureValidState();

        if (!OstUser.CONST_STATUS.ACTIVATED.equalsIgnoreCase(
                OstUser.getById(mUserId).getStatus()
        )) {
            throw new OstError("owfa_evs_rp_1", OstErrors.ErrorCode.USER_NOT_ACTIVATED);
        }
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();
        Bundle bundle = getIntent().getExtras();
        FragmentUtils.addFragment(R.id.layout_container,
                ResetPinFragment.newInstance(bundle),
                this);

    }

    @Override
    OstWorkflowContext getWorkflowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.RESET_PIN);
    }
}