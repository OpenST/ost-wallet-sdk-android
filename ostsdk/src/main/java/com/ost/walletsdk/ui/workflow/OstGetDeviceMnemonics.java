package com.ost.walletsdk.ui.workflow;

import android.os.Bundle;
import android.view.WindowManager;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.ui.viewmnemonics.ViewMnemonicsFragment;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import org.json.JSONObject;

public class OstGetDeviceMnemonics extends OstWorkFlowActivity {

    final JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("view_mnemonics");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting FLAG_SECURE flag to protect view against screen recording and screen shotting
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    void ensureValidState() {
        super.ensureValidState();

        if (!OstDevice.CONST_STATUS.AUTHORIZED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            throw new OstError("owfa_evs_gdm_1", OstErrors.ErrorCode.DEVICE_UNAUTHORIZED);
        }
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();

        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("initial_loader")).getString());
        getWorkflowLoader().onInitLoader();

        OstSdk.getDeviceMnemonics(mUserId, mWorkFlowListener);
    }

    @Override
    JSONObject getContentString(OstWorkflowContext ostWorkflowContext) {
        return ContentConfig.getInstance()
                .getStringConfig("view_mnemonics")
                .optJSONObject("get_pin");
    }

    @Override
    public boolean flowComplete(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        showProgress(false);
        byte[] mnemonics = (byte[]) ostContextEntity.getEntity();
        ViewMnemonicsFragment fragment = ViewMnemonicsFragment.newInstance(new String(mnemonics));
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
        return false;
    }

    @Override
    OstWorkflowContext getWorkflowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.GET_DEVICE_MNEMONICS);
    }

    @Override
    public void popTopFragment() {
        super.popTopFragment();

        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("loader")).getString());
        getWorkflowLoader().onPostAuthentication();
    }
}