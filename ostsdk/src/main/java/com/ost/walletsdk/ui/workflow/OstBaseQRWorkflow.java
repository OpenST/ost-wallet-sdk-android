package com.ost.walletsdk.ui.workflow;

import android.content.Intent;
import android.net.Uri;

import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.OstVerifyDeviceFragment;
import com.ost.walletsdk.ui.qrscanner.QRScannerFragment;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

import org.json.JSONObject;

abstract class OstBaseQRWorkflow  extends OstWorkFlowActivity implements
        QRScannerFragment.OnFragmentInteractionListener,
        OstVerifyDeviceFragment.OnFragmentInteractionListener {

    private static final String LOG_TAG = "OstADVQRWorkflow";
    final JSONObject contentConfig = getContentConfig();
    private String mQrPayload;

    protected abstract JSONObject getContentConfig();

    private QRScannerFragment mQrScannerFragment;

    @Override
    void ensureValidState() {
        super.ensureValidState();

        if (!OstDevice.CONST_STATUS.AUTHORIZED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            throw new OstError("owfa_evs_advqr_1", OstErrors.ErrorCode.DEVICE_UNAUTHORIZED);
        }
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();

        mQrPayload = getIntent().getStringExtra(QR_PAYLOAD);
        if (null != mQrPayload) {
            final Intent intent = new Intent();
            intent.setData(Uri.parse(mQrPayload));
            onResultString(intent);
            return;
        }

        final String scanQRTitle = StringConfig.instance(
                contentConfig.optJSONObject("scan_qr").optJSONObject("title_label")
        ).getString();

        mQrScannerFragment = QRScannerFragment.newInstance(scanQRTitle);
        FragmentUtils.clearBackStackAndAddFragment(R.id.layout_container,
                mQrScannerFragment,
                this);
    }

    @Override
    JSONObject getContentString(OstWorkflowContext ostWorkflowContext) {
        return contentConfig.optJSONObject("get_pin");
    }

    @Override
    OstWorkflowContext getWorkflowContext() {
        return new OstWorkflowContext(OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_DEVICE_WITH_QR_CODE);
    }

    @Override
    public abstract void onResultString(Intent data);

    @Override
    public abstract boolean verifyData(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface);

    @Override
    public void popTopFragment() {
        super.popTopFragment();

        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("loader")).getString());
        getWorkflowLoader().onPostAuthentication(contentConfig);
    }

    @Override
    public void onDataVerified() {
        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("loader")).getString());
        getWorkflowLoader().onPostAuthentication(contentConfig);
    }

    @Override
    public void onDataRejected() {

    }

    @Override
    boolean showBackButton() {
        return true;
    }

    @Override
    public boolean flowInterrupt(String workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        if (isCrossButtonClicked(ostError) ||
                !OstErrors.ErrorCode.WORKFLOW_CANCELLED.equals(ostError.getErrorCode()) ||
                null != mQrPayload) {
            return super.flowInterrupt(workflowId, ostWorkflowContext, ostError);
        }
        mQrScannerFragment.restartScanning();
        showProgress(false);
        return true;
    }
}
