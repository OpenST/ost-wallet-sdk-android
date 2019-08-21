package com.ost.walletsdk.ui.workflow;

import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.entermnemonics.EnterMnemonicsFragment;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import org.json.JSONObject;

public class OstAuthorizeDeviceMnemonics extends OstWorkFlowActivity {

    final JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("add_current_device_with_mnemonics");

    @Override
    void ensureValidState() {
        super.ensureValidState();

        if (!OstUser.CONST_STATUS.ACTIVATED.equalsIgnoreCase(
                OstUser.getById(mUserId).getStatus()
        )) {
            throw new OstError("owfa_evs_adwm_1", OstErrors.ErrorCode.USER_NOT_ACTIVATED);
        }

        if (!OstDevice.CONST_STATUS.REGISTERED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            throw new OstError("owfa_evs_adwm_2", OstErrors.ErrorCode.DEVICE_CAN_NOT_BE_AUTHORIZED);
        }
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();
        EnterMnemonicsFragment fragment = EnterMnemonicsFragment.newInstance(getIntent().getExtras());
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    JSONObject getContentString(OstWorkflowContext ostWorkflowContext) {
        return ContentConfig.getInstance()
                .getStringConfig("add_current_device_with_mnemonics")
                .optJSONObject("get_pin");
    }

    @Override
    OstWorkflowContext getWorkflowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_DEVICE_WITH_MNEMONICS);
    }

    @Override
    public void popTopFragment() {
        super.popTopFragment();

        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("loader")).getString());
    }

    @Override
    boolean showBackButton() {
        return true;
    }

    @Override
    public boolean flowInterrupt(String workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        if (!OstErrors.ErrorCode.WORKFLOW_CANCELLED.equals(ostError.getErrorCode())) {
            return super.flowInterrupt(workflowId, ostWorkflowContext, ostError);
        }
        showProgress(false);
        return true;
    }
}