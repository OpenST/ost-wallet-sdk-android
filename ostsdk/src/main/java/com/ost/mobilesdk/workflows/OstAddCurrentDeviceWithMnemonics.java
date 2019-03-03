package com.ost.mobilesdk.workflows;

import android.os.Bundle;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstDeviceManager;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.security.OstMultiSigSigner;
import com.ost.mobilesdk.security.structs.SignedAddDeviceStruct;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;
import com.ost.mobilesdk.workflows.services.OstDevicePollingService;
import com.ost.mobilesdk.workflows.services.OstPollingService;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;


public class OstAddCurrentDeviceWithMnemonics extends OstBaseUserAuthenticatorWorkflow implements OstPinAcceptInterface {

    private static final String TAG = "OstADWithMnemonics";
    private final byte[] mMnemonics;
    SignedAddDeviceStruct signedData;
    String mAddedDeviceAddress;

    public OstAddCurrentDeviceWithMnemonics(String userId, byte[] mnemonics, OstWorkFlowCallback callback) {
        super(userId, callback);
        mMnemonics = mnemonics;
    }

    @Override
    boolean hasValidParams() {
        return super.hasValidParams() && !(mMnemonics.length < 1);
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.ADD_DEVICE_WITH_MNEMONICS;
    }

    protected AsyncStatus performUserDeviceValidation(Object stateObject) {



        try {
            ensureDeviceManager();
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        }
        return super.performUserDeviceValidation(stateObject);
    }

    @Override
    AsyncStatus performOnAuthenticated() {
        try {
            mOstApiClient.getDeviceManager();
        } catch (IOException e) {
            return postErrorInterrupt("wf_adwm_pr_7", OstErrors.ErrorCode.ADD_DEVICE_API_FAILED);
        }

        String deviceAddress = mOstUser.getCurrentDevice().getAddress();
        String deviceManagerAddress = OstUser.getById(mUserId).getDeviceManagerAddress();
        OstMultiSigSigner signer = null;
        try {
            signer = new OstMultiSigSigner(mUserId);
            signedData = signer.addCurrentDeviceWithMnemonics(mMnemonics);
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        }

        String signature = signedData.getSignature();
        String signerAddress = signedData.getSignerAddress();
        mAddedDeviceAddress = signedData.getDeviceToBeAdded();



        Log.i(TAG, "Api Call payload");
        AsyncStatus apiCallStatus = makeAddDeviceCall(signature, signerAddress, deviceManagerAddress, deviceAddress);

        if ( apiCallStatus.isSuccess() ) {
            //request acknowledge
            postRequestAcknowledge(new OstWorkflowContext(getWorkflowType()),
                    new OstContextEntity(OstDevice.getById(mAddedDeviceAddress), OstSdk.DEVICE));

            //increment nonce
            OstDeviceManager.getById(mOstUser.getDeviceManagerAddress()).incrementNonce();

            //Start the polling
            return startPolling();
        }
        return apiCallStatus;
    }

    AsyncStatus startPolling() {
        OstDevicePollingService.startPolling(mUserId, mAddedDeviceAddress, OstDevice.CONST_STATUS.AUTHORIZED,
                OstDevice.CONST_STATUS.REGISTERED);

        Log.i(TAG, "Waiting for update");
        Bundle bundle = waitForUpdate(OstSdk.DEVICE, mAddedDeviceAddress);

        boolean hasTimedout = bundle.getBoolean(OstPollingService.EXTRA_IS_POLLING_TIMEOUT, true);
        if ( hasTimedout ) {
            Log.d(TAG, String.format("Polling time out for device Id: %s", mAddedDeviceAddress));
            return postErrorInterrupt("wf_adwm_pr_5", OstErrors.ErrorCode.POLLING_TIMEOUT);
        }

        Log.i(TAG, "Response received for Add device");
        return postFlowComplete();
    }

    @Override
    protected boolean shouldCheckCurrentDeviceAuthorization() {
        return false;
    }

}