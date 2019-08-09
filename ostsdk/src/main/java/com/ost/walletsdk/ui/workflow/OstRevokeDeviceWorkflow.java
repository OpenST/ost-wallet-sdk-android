package com.ost.walletsdk.ui.workflow;

import android.os.Bundle;
import android.text.TextUtils;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.managedevices.DeviceListFragment;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import org.json.JSONObject;

import static com.ost.walletsdk.ui.recovery.RecoveryFragment.DEVICE_ADDRESS;
import static com.ost.walletsdk.ui.recovery.RecoveryFragment.SHOW_BACK_BUTTON;

public class OstRevokeDeviceWorkflow extends OstWorkFlowActivity {

    @Override
    boolean invalidState() {
        if (super.invalidState()) return true;

        if (!OstUser.CONST_STATUS.ACTIVATED.equalsIgnoreCase(
                OstUser.getById(mUserId).getStatus()
        )) {
            mWorkFlowListener.flowInterrupt(
                    new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.REVOKE_DEVICE),
                    new OstError("owfa_oc_rd_1", OstErrors.ErrorCode.USER_NOT_ACTIVATED)
            );
            finish();
            return true;
        }
        return false;
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();
        String deviceAddress = getIntent().getStringExtra(DEVICE_ADDRESS);
        if (TextUtils.isEmpty(deviceAddress)) {
            Bundle bundle = getIntent().getExtras();
            bundle.putBoolean(SHOW_BACK_BUTTON, false);
            DeviceListFragment fragment = DeviceListFragment.revokeDeviceInstance(bundle);
            FragmentUtils.addFragment(R.id.layout_container,
                    fragment,
                    this);
            fragment.contentConfig = ContentConfig.getInstance().getStringConfig("revoke_device").optJSONObject("device_list");
        } else {
            OstSdk.revokeDevice(mUserId, deviceAddress, mWorkFlowListener);
        }
    }

    @Override
    JSONObject getContentString(OstWorkflowContext ostWorkflowContext) {
        return ContentConfig.getInstance()
                .getStringConfig("revoke_device")
                .optJSONObject("get_pin");
    }
}