package com.ost.walletsdk.ui.workflow;

import android.os.Bundle;
import android.text.TextUtils;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.managedevices.Device;
import com.ost.walletsdk.ui.managedevices.DeviceListFragment;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import org.json.JSONObject;

import static com.ost.walletsdk.ui.recovery.RecoveryFragment.DEVICE_ADDRESS;
import static com.ost.walletsdk.ui.recovery.RecoveryFragment.SHOW_BACK_BUTTON;

public class OstRevokeDeviceWorkflow extends OstWorkFlowActivity {

    private boolean mShowBackButton = false;
    final JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("revoke_device");

    @Override
    void ensureValidState() {
        super.ensureValidState();

        if (!OstDevice.CONST_STATUS.AUTHORIZED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            throw new OstError("owfa_evs_rd_1", OstErrors.ErrorCode.DEVICE_UNAUTHORIZED);
        }
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();
        String deviceAddress = getIntent().getStringExtra(DEVICE_ADDRESS);
        if (TextUtils.isEmpty(deviceAddress)) {
            mShowBackButton = true;
            Bundle bundle = getIntent().getExtras();
            bundle.putBoolean(SHOW_BACK_BUTTON, false);
            DeviceListFragment fragment = DeviceListFragment.revokeDeviceInstance(bundle);
            FragmentUtils.addFragment(R.id.layout_container,
                    fragment,
                    this);
            fragment.contentConfig = ContentConfig.getInstance().getStringConfig("revoke_device");
        } else {
            mShowBackButton = false;
            showProgress(true, StringConfig.instance(contentConfig.optJSONObject("initial_loader")).getString());
            OstSdk.revokeDevice(mUserId, deviceAddress, mWorkFlowListener);
        }
    }

    @Override
    JSONObject getContentString(OstWorkflowContext ostWorkflowContext) {
        return ContentConfig.getInstance()
                .getStringConfig("revoke_device")
                .optJSONObject("get_pin");
    }

    @Override
    OstWorkflowContext getWorkflowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.REVOKE_DEVICE);
    }

    @Override
    public void onDeviceSelectToRevoke(Device device) {
        super.onDeviceSelectToRevoke(device);
        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("initial_loader")).getString());
    }

    @Override
    public void popTopFragment() {
        super.popTopFragment();

        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("loader")).getString());
    }

    @Override
    boolean showBackButton() {
        return mShowBackButton;
    }

    @Override
    public boolean flowInterrupt(String workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        if (!mShowBackButton || !OstErrors.ErrorCode.WORKFLOW_CANCELLED.equals(ostError.getErrorCode())) {
            return super.flowInterrupt(workflowId, ostWorkflowContext, ostError);
        }
        showProgress(false);
        return true;
    }
}