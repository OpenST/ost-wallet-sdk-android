package com.ost.walletsdk.ui.workflow;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.ui.OstVerifyTxnFragment;
import com.ost.walletsdk.ui.qrscanner.QRScannerFragment;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.ui.util.DialogFactory;
import com.ost.walletsdk.ui.util.FragmentUtils;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class OstExecuteTxnViaQRWorkflow extends OstWorkFlowActivity implements
        QRScannerFragment.OnFragmentInteractionListener,
        OstVerifyTxnFragment.OnFragmentInteractionListener {

    private static final String LOG_TAG = "OstETVQRWorkflow";
    final JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("execute_transaction_via_qr");
    private QRScannerFragment mQrScannerFragment;

    @Override
    void ensureValidState() {
        super.ensureValidState();

        if (!OstDevice.CONST_STATUS.AUTHORIZED.equalsIgnoreCase(
                OstUser.getById(mUserId).getCurrentDevice().getStatus()
        )) {
            throw new OstError("owfa_evs_etvqr_1", OstErrors.ErrorCode.DEVICE_UNAUTHORIZED);
        }
    }

    @Override
    void initiateWorkFlow() {
        super.initiateWorkFlow();
        mQrScannerFragment = QRScannerFragment.newInstance("Scan QR Code");
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
        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("initial_loader")).getString());
        if (data != null && data.getData() != null) {
            String returnedResult = data.getData().toString();

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

        if (!(ostContextEntity.getEntity() instanceof JSONObject)) {
            ostVerifyDataInterface.cancelFlow();
            DialogFactory.createSimpleOkErrorDialog(OstExecuteTxnViaQRWorkflow.this
                    , "Invalid QR-Code"
                    , "QR-Code scanned for execute Transaction is invalid. Please scan valid QR-Code to execute transaction."
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mQrScannerFragment.onResume();
                        }
                    }
            ).show();
            return true;
        }

        OstVerifyTxnFragment bottomSheet = new OstVerifyTxnFragment();
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());

        bottomSheet.setDataToVerify((JSONObject) ostContextEntity.getEntity());
        bottomSheet.setVerifyDataCallback(ostVerifyDataInterface);
        bottomSheet.setStringConfig(contentConfig.optJSONObject("verify_transaction"));
        return true;
    }

    @Override
    public void popTopFragment() {
        super.popTopFragment();

        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("loader")).getString());
    }

    @Override
    public void onDataVerified() {
        showProgress(true, StringConfig.instance(contentConfig.optJSONObject("initial_loader")).getString());
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
        if (!OstErrors.ErrorCode.WORKFLOW_CANCELLED.equals(ostError.getErrorCode())) {
            return super.flowInterrupt(workflowId, ostWorkflowContext, ostError);
        }
        mQrScannerFragment.onResume();
        showProgress(false);
        return true;
    }
}