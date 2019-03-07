package com.ost.mobilesdk.workflows;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.security.OstMultiSigSigner;
import com.ost.mobilesdk.security.structs.SignedAddDeviceStruct;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.mobilesdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstDevicePollingService;
import com.ost.mobilesdk.workflows.services.OstPollingService;

import org.web3j.crypto.WalletUtils;

import java.io.IOException;
import java.util.ArrayList;


public class OstAddDeviceWithQR extends OstBaseUserAuthenticatorWorkflow implements OstVerifyDataInterface {

    private static final String TAG = "OstAddDeviceWithQR";
    private final String mDeviceAddressToBeAdded;

    public OstAddDeviceWithQR(String userId, String deviceAddress, OstWorkFlowCallback callback) {
        super(userId, callback);
        mDeviceAddressToBeAdded = deviceAddress;
    }

    @Override
    protected void setStateManager() {
        super.setStateManager();
        ArrayList<String> orderedStates = stateManager.orderedStates;
        int paramsValidationIndx = orderedStates.indexOf(WorkflowStateManager.PARAMS_VALIDATED);
        //Add custom states.
        orderedStates.add(paramsValidationIndx + 1, "VERIFY_DATA");
        orderedStates.add(paramsValidationIndx + 2, "DATA_VERIFIED");
    }

    @Override
    protected AsyncStatus onStateChanged(String state, Object stateObject) {
        try {
            switch (state) {
                case WorkflowStateManager.INITIAL:
                    if (!hasValidParams()) {
                        return postErrorInterrupt("wf_adwq_pr_1", ErrorCode.INVALID_WORKFLOW_PARAMS);
                    }
                    return super.onStateChanged(state,stateObject);
                case WorkflowStateManager.DEVICE_VALIDATED:
                    if (!hasValidAddress(mDeviceAddressToBeAdded)) {
                        return postErrorInterrupt("wf_adwq_pr_2", ErrorCode.INVALID_ADD_DEVICE_ADDRESS);
                    }
                    return super.onStateChanged(state,stateObject);
                case "VERIFY_DATA":
                    postVerifyData(new OstContextEntity(OstDevice.getById(mDeviceAddressToBeAdded), OstSdk.DEVICE),
                            OstAddDeviceWithQR.this);
                    return new AsyncStatus(true);
                case "DATA_VERIFIED":
                    return performNext();
            }
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        } catch (Throwable th) {
            OstError ostError = new OstError("bua_wf_osc_1", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            ostError.setStackTrace(th.getStackTrace());
            return postErrorInterrupt(ostError);
        }
        return super.onStateChanged(state, stateObject);
    }

    @Override
    public void dataVerified() {
        stateManager.setState("DATA_VERIFIED");
        perform();
    }


    @Override
    AsyncStatus performOnAuthenticated() {
        try {
            mOstApiClient.getDeviceManager();
        } catch (IOException e) {
            return postErrorInterrupt("wf_adwq_pr_7", ErrorCode.ADD_DEVICE_API_FAILED);
        }

        String deviceAddress = mDeviceAddressToBeAdded;
        OstMultiSigSigner ostMultiSigSigner = new OstMultiSigSigner(mUserId);
        SignedAddDeviceStruct signedData = ostMultiSigSigner.addExternalDevice(deviceAddress);

        Log.i(TAG, "Api Call payload");
        AsyncStatus apiCallStatus = makeAddDeviceCall(signedData);

        if (!apiCallStatus.isSuccess()) {
            return postErrorInterrupt("wf_adwq_pr_4", ErrorCode.ADD_DEVICE_API_FAILED);
        }

        //request acknowledge
        postRequestAcknowledge(new OstWorkflowContext(getWorkflowType()),
                new OstContextEntity(OstDevice.getById(mDeviceAddressToBeAdded), OstSdk.DEVICE));


        return pollForStatus();
    }

    private AsyncStatus pollForStatus() {
        Log.i(TAG, "Waiting for update");
        Bundle bundle = OstDevicePollingService.startPolling(mUserId, mDeviceAddressToBeAdded, OstDevice.CONST_STATUS.AUTHORIZED,
                OstDevice.CONST_STATUS.CREATED);
        if (bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true)) {
            Log.d(TAG, String.format("Polling time out for device Id: %s", mDeviceAddressToBeAdded));
            return postErrorInterrupt("wf_adwq_pr_5", ErrorCode.POLLING_TIMEOUT);
        }

        Log.i(TAG, "Response received for Add device");
        return postFlowComplete();
    }

    @Override
    void ensureValidParams() {
        if ( TextUtils.isEmpty(mDeviceAddressToBeAdded) || !WalletUtils.isValidAddress(mDeviceAddressToBeAdded) ) {
            throw new OstError("wf_ad_evp_1", ErrorCode.INVALID_WORKFLOW_PARAMS);
        }
        super.ensureValidParams();
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.ADD_DEVICE_WITH_QR;
    }
}