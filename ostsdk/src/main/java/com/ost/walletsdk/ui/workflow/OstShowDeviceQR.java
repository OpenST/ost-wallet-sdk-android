package com.ost.walletsdk.ui.workflow;

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.network.OstJsonApi;
import com.ost.walletsdk.network.OstJsonApiCallback;
import com.ost.walletsdk.network.polling.OstDevicePollingHelper;
import com.ost.walletsdk.network.polling.interfaces.OstPollingCallback;
import com.ost.walletsdk.ui.qrfragment.QRFragment;
import com.ost.walletsdk.ui.sdkInteract.WorkFlowListener;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.ui.util.DialogFactory;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import org.json.JSONObject;

public class OstShowDeviceQR extends OstWorkFlowActivity implements
        QRFragment.OnFragmentInteractionListener,
        OstJsonApiCallback {

    final JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("show_add_device_qr");
    final JSONObject qrContentConfig = contentConfig.optJSONObject("show_qr");
    final String loaderString = StringConfig.instance(contentConfig.optJSONObject("loader")).getString();

    @Override
    void ensureValidState() {
        super.ensureValidState();
        if (!OstUser.CONST_STATUS.ACTIVATED.equalsIgnoreCase(
                OstUser.getById(mUserId).getStatus()
        )) {
            throw new OstError("owfa_evs_sdqr_1", OstErrors.ErrorCode.USER_NOT_ACTIVATED);
        }

        if (!OstDevice.CONST_STATUS.REGISTERED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            throw new OstError("owfa_evs_sdqr_2", OstErrors.ErrorCode.DEVICE_CAN_NOT_BE_AUTHORIZED);
        }
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();
        QRFragment fragment = QRFragment.newInstance(mUserId);
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
        fragment.setContentConfig(qrContentConfig);
    }

    private void showFeedback() {
        showProgress(false);
        OstDevice ostDevice = OstUser.getById(mUserId).getCurrentDevice();
        if (OstDevice.CONST_STATUS.AUTHORIZED
                .equalsIgnoreCase(
                        ostDevice.getStatus()
                ) || OstDevice.CONST_STATUS.AUTHORIZING
                .equalsIgnoreCase(
                        ostDevice.getStatus()
                )) {

            mWorkFlowListener.requestAcknowledged(
                    new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.SHOW_DEVICE_QR),
                    new OstContextEntity(ostDevice, OstSdk.DEVICE)
            );
            new OstDevicePollingHelper(
                    mUserId,
                    ostDevice.getAddress(),
                    new OstDevicePollingCallbackImpl(ostDevice.getAddress(), mWorkFlowListener)
            );

        } else {
            String title = qrContentConfig.optJSONObject("unauthorized_alert").optString("title");
            String message = qrContentConfig.optJSONObject("unauthorized_alert").optString("message");
            DialogFactory.createSimpleOkErrorDialog(this, title, message).show();
        }
    }

    @Override
    public void onCheckDevice() {
        showProgress(true, loaderString);
        OstJsonApi.getCurrentDevice(mUserId, this);
    }

    @Override
    public void onOstJsonApiSuccess(@Nullable JSONObject data) {
        showFeedback();
    }

    @Override
    public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject response) {
        showProgress(false);
        String title = qrContentConfig.optJSONObject("api_failure_alert").optString("title");
        String message = qrContentConfig.optJSONObject("api_failure_alert").optString("message");
        DialogFactory.createSimpleOkErrorDialog(this, title, message).show();
    }

    @Override
    OstWorkflowContext getWorkflowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.SHOW_DEVICE_QR);
    }

    static class OstDevicePollingCallbackImpl implements OstPollingCallback {

        private final WorkFlowListener mWorkflowListener;
        private final String mDeviceAddress;

        OstDevicePollingCallbackImpl(String deviceAddress, WorkFlowListener workflowListener) {
            this.mDeviceAddress = deviceAddress;
            this.mWorkflowListener = workflowListener;
        }
        @Override
        public void onOstPollingSuccess(@Nullable OstBaseEntity entity, @Nullable JSONObject data) {

            mWorkflowListener.flowComplete(
                    new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.SHOW_DEVICE_QR),
                    new OstContextEntity(OstDevice.getById(mDeviceAddress), OstSdk.DEVICE)
            );
        }

        @Override
        public void onOstPollingFailed(OstError error) {

            mWorkflowListener.flowInterrupt(
                    new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.SHOW_DEVICE_QR),
                    error
            );
        }
    }
}