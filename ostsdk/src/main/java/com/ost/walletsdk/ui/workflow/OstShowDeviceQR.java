package com.ost.walletsdk.ui.workflow;

import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.qrfragment.QRFragment;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

public class OstShowDeviceQR extends OstWorkFlowActivity implements QRFragment.OnFragmentInteractionListener {

    @Override
    boolean invalidState() {
        if (super.invalidState()) return true;

        if (!OstUser.CONST_STATUS.ACTIVATED.equalsIgnoreCase(
                OstUser.getById(mUserId).getStatus()
        )) {
            mWorkFlowListener.flowInterrupt(
                    getWorkflowContext(),
                    new OstError("owfa_oc_sdqr_1", OstErrors.ErrorCode.USER_NOT_ACTIVATED)
            );
            finish();
            return true;
        }

        if (!OstDevice.CONST_STATUS.REGISTERED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            mWorkFlowListener.flowInterrupt(
                    getWorkflowContext(),
                    new OstError("owfa_oc_sdqr_2", OstErrors.ErrorCode.DEVICE_CAN_NOT_BE_AUTHORIZED)
            );
            finish();
            return true;
        }
        return false;
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();
        QRFragment fragment = QRFragment.newInstance(mUserId);
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public boolean flowComplete(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        super.flowComplete(workflowId, ostWorkflowContext, ostContextEntity);
        showFeedback();
        return false;
    }

    private void showFeedback() {
        showProgress(false);
        if (OstDevice.CONST_STATUS.AUTHORIZED
                .equalsIgnoreCase(
                       OstUser.getById(mUserId).getCurrentDevice().getStatus()
                )) {
            showToastMessage("Device is Authorized", true);
        } else if (OstDevice.CONST_STATUS.AUTHORIZING
                .equalsIgnoreCase(
                        OstUser.getById(mUserId).getCurrentDevice().getStatus()
                )) {
            showToastMessage("Device is still Authorizing", false);
        } else if (OstDevice.CONST_STATUS.REGISTERED
                .equalsIgnoreCase(
                        OstUser.getById(mUserId).getCurrentDevice().getStatus()
                )) {
            showToastMessage("Device is still in Registered state", false);
        } else {
            showToastMessage("Device is still in InConsistent state", false);
        }
    }

    @Override
    public void onCheckDevice() {
        //Todo:: Check Device Status
    }
}