package com.ost.walletsdk.ui.workflow;

import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.ui.walletsetup.WalletSetUpFragment;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

public class OstActivateWorkflow extends OstWorkFlowActivity {


    @Override
    void ensureValidState() {
        super.ensureValidState();

        if (!OstUser.CONST_STATUS.CREATED.equalsIgnoreCase(
                OstUser.getById(mUserId).getStatus()
        )) {
            throw new OstError("owfa_evs_au_1", OstErrors.ErrorCode.USER_ALREADY_ACTIVATED);
        }

        if (!OstDevice.CONST_STATUS.REGISTERED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            throw new OstError("owfa_evs_au_2", OstErrors.ErrorCode.DEVICE_NOT_REGISTERED);
        }
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();

        FragmentUtils.addFragment(R.id.layout_container,
                WalletSetUpFragment.newInstance(getIntent().getExtras()),
                this);
    }

    @Override
    OstWorkflowContext getWorkflowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER);
    }
}