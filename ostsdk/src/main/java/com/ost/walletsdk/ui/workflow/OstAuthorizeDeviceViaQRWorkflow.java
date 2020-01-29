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

public class OstAuthorizeDeviceViaQRWorkflow extends OstBaseQRWorkflow {

    private static final String LOG_TAG = "OstADVQRWorkflow";

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
            getWorkflowLoader().onInitLoader(contentConfig);
            try {
                OstSdk.performQRAction(
                        mUserId,
                        returnedResult,
                        mWorkFlowListener
                );
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Exception in Data;");
                showProgress(false);
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
}