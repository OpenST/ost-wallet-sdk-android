package com.ost.walletsdk.ui.workflow;

import android.content.Intent;
import android.util.Log;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.OstVerifyDeviceFragment;
import com.ost.walletsdk.ui.qrscanner.QRScannerFragment;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class OstAuthorizeDeviceViaQRWorkflow extends OstWorkFlowActivity implements
        QRScannerFragment.OnFragmentInteractionListener,
        OstVerifyDeviceFragment.OnFragmentInteractionListener {

    private static final String LOG_TAG = "OstADVQRWorkflow";
    final JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("scan_qr_to_authorize_device");
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
    public void onResultString(Intent data) {
        Log.d(LOG_TAG, String.format("QR process result %s", data));
        if (data != null && data.getData() != null) {
            String returnedResult = data.getData().toString();

            //QR Validation check
            try {
                if (!OstConstants.DATA_DEFINITION_AUTHORIZE_DEVICE.equalsIgnoreCase(
                        new JSONObject(returnedResult).getString(OstConstants.QR_DATA_DEFINITION
                        ))) {
                    throw new Exception("Invalid QR");
                }
            } catch (Exception exception) {
                if (null != mWorkFlowListener) mWorkFlowListener.flowInterrupt(getWorkflowContext(), new OstError("oadvqrw_ors_advqr_1", OstErrors.ErrorCode.INVALID_QR_CODE));
                return;
            }

            //Start workflow
            showProgress(true, StringConfig.instance(contentConfig.optJSONObject("initial_loader")).getString());
            try {
                OstSdk.performQRAction(
                        mUserId,
                        returnedResult,
                        mWorkFlowListener
                );
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Exception in Data;");
                showProgress(false);
                showToastMessage("QR Reading failed.. Try Again", false);
            }
        }
    }

    @Override
    public boolean verifyData(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {
        showProgress(false);

        OstVerifyDeviceFragment bottomSheet = new OstVerifyDeviceFragment();
        bottomSheet.setCancelable(false);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        OstDevice ostDevice = ((OstDevice) ostContextEntity.getEntity());
        bottomSheet.setDataToVerify(ostDevice);
        bottomSheet.setVerifyDataCallback(ostVerifyDataInterface);
        bottomSheet.setStringConfig(contentConfig.optJSONObject("verify_device"));
        return true;
    }

    @Override
    public void popTopFragment() {
        super.popTopFragment();

        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("loader")).getString());
    }

    @Override
    public void onDataVerified() {
        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("loader")).getString());
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
        if (isCrossButtonClicked(ostError) || !OstErrors.ErrorCode.WORKFLOW_CANCELLED.equals(ostError.getErrorCode())) {
            return super.flowInterrupt(workflowId, ostWorkflowContext, ostError);
        }
        mQrScannerFragment.restartScanning();
        showProgress(false);
        return true;
    }
}