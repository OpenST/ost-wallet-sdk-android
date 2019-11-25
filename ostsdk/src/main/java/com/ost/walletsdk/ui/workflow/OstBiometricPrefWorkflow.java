package com.ost.walletsdk.ui.workflow;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import org.json.JSONObject;

public class OstBiometricPrefWorkflow extends OstWorkFlowActivity {

    final JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("biometric_preference");

    @Override
    void ensureValidState() {
        super.ensureValidState();

        if (!OstDevice.CONST_STATUS.AUTHORIZED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            throw new OstError("owfa_evs_ubp_1", OstErrors.ErrorCode.DEVICE_UNAUTHORIZED);
        }
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();

        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("initial_loader")).getString());
        getWorkflowLoader().onInitLoader(contentConfig);

        boolean enable = getIntent().getBooleanExtra(OstWorkFlowActivity.ENABLE, false);
        OstSdk.updateBiometricPreference(mUserId, enable, mWorkFlowListener);
    }

    @Override
    JSONObject getContentString(OstWorkflowContext ostWorkflowContext) {
        return ContentConfig.getInstance()
                .getStringConfig("biometric_preference")
                .optJSONObject("get_pin");
    }

    @Override
    OstWorkflowContext getWorkflowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.UPDATE_BIOMETRIC_PREFERENCE);
    }

    @Override
    public void popTopFragment() {
        super.popTopFragment();

        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("loader")).getString());
        getWorkflowLoader().onPostAuthentication(contentConfig);
    }
}