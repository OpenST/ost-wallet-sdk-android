package com.ost.walletsdk.ui.workflow;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.ui.viewmnemonics.ViewMnemonicsFragment;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import org.json.JSONObject;

public class OstGetDeviceMnemonics extends OstWorkFlowActivity {

    @Override
    boolean invalidState() {
        if (super.invalidState()) return true;

        if (!OstDevice.CONST_STATUS.AUTHORIZED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            mWorkFlowListener.flowInterrupt(
                    getWorkflowContext(),
                    new OstError("owfa_oc_gdm_1", OstErrors.ErrorCode.DEVICE_UNAUTHORIZED)
            );
            finish();
            return true;
        }

        return false;
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();
        showProgress(true, "Getting device mnemonics...");
        OstSdk.getDeviceMnemonics(mUserId, mWorkFlowListener);
    }

    @Override
    JSONObject getContentString(OstWorkflowContext ostWorkflowContext) {
        return ContentConfig.getInstance()
                .getStringConfig("view_mnemonics")
                .optJSONObject("get_pin");
    }

    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        showProgress(false);
        byte[] mnemonics = (byte[]) ostContextEntity.getEntity();
        ViewMnemonicsFragment fragment = ViewMnemonicsFragment.newInstance(new String(mnemonics));
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    OstWorkflowContext getWorkflowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.GET_DEVICE_MNEMONICS);
    }
}