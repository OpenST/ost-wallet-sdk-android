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
    boolean invalidState() {
        if  (super.invalidState()) return true;

        if (!OstUser.CONST_STATUS.CREATED.equalsIgnoreCase(
                OstUser.getById(mUserId).getStatus()
        )) {
            mWorkFlowListener.flowInterrupt(
                    getWorkflowContext(),
                    new OstError("owfa_oc_au_1", OstErrors.ErrorCode.USER_ALREADY_ACTIVATED)
            );
            finish();
            return true;
        }

        if (!OstDevice.CONST_STATUS.REGISTERED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            mWorkFlowListener.flowInterrupt(
                    getWorkflowContext(),
                    new OstError("owfa_oc_au_2", OstErrors.ErrorCode.DEVICE_NOT_REGISTERED)
            );
            finish();
            return true;
        }
        return false;
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