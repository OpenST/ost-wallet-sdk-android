package com.ost.walletsdk.ui.workflow;

import android.os.Bundle;
import android.text.TextUtils;

import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.managedevices.DeviceListFragment;
import com.ost.walletsdk.ui.recovery.InitiateRecoveryFragment;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import static com.ost.walletsdk.ui.recovery.RecoveryFragment.DEVICE_ADDRESS;
import static com.ost.walletsdk.ui.recovery.RecoveryFragment.SHOW_BACK_BUTTON;

public class OstInitiateRecoveryWorkflow extends OstWorkFlowActivity {

    private boolean mShowBackButton = false;

    @Override
    void ensureValidState() {
        super.ensureValidState();

        if (!OstUser.CONST_STATUS.ACTIVATED.equalsIgnoreCase(
                OstUser.getById(mUserId).getStatus()
        )) {
            throw new OstError("owfa_evs_ir_1", OstErrors.ErrorCode.USER_NOT_ACTIVATED);
        }

        if (!OstDevice.CONST_STATUS.REGISTERED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            throw new OstError("owfa_evs_ir_2", OstErrors.ErrorCode.DEVICE_CAN_NOT_BE_AUTHORIZED);
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
            DeviceListFragment fragment = DeviceListFragment.initiateRecoveryInstance(bundle);
            FragmentUtils.addFragment(R.id.layout_container,
                    fragment,
                    this);
            fragment.contentConfig = ContentConfig.getInstance().getStringConfig("initiate_recovery");
        } else {
            mShowBackButton = false;
            Bundle bundle = getIntent().getExtras();
            bundle.putBoolean(SHOW_BACK_BUTTON, false);
            FragmentUtils.addFragment(R.id.layout_container,
                    InitiateRecoveryFragment.newInstance(bundle),
                    this);
        }
    }

    @Override
    OstWorkflowContext getWorkflowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.INITIATE_DEVICE_RECOVERY);
    }

    @Override
    boolean showBackButton() {
        return mShowBackButton;
    }

    @Override
    public boolean flowInterrupt(String workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        if (isCrossButtonClicked(ostError) || !mShowBackButton || !OstErrors.ErrorCode.WORKFLOW_CANCELLED.equals(ostError.getErrorCode())) {
            return super.flowInterrupt(workflowId, ostWorkflowContext, ostError);
        }
        showProgress(false);
        return false;
    }
}