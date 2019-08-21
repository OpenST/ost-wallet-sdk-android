package com.ost.walletsdk.ui.workflow;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.network.OstJsonApi;
import com.ost.walletsdk.network.OstJsonApiCallback;
import com.ost.walletsdk.ui.qrfragment.QRFragment;
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
            mWorkFlowListener.flowComplete(
                    new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.SHOW_DEVICE_QR),
                    new OstContextEntity(ostDevice, OstSdk.DEVICE)
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
}