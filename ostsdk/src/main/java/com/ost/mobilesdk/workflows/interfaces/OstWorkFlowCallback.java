package com.ost.mobilesdk.workflows.interfaces;

import android.graphics.Bitmap;

import com.ost.mobilesdk.workflows.OstContextEntity;
import com.ost.mobilesdk.workflows.OstWorkflowContext;
import com.ost.mobilesdk.workflows.errors.OstError;

import org.json.JSONObject;

/**
 * OstWorkFlowCallback implemented by SDK user to perform prerequisites task.
 * These tasks are assigned by SDK workflows with help of callbacks.
 *
 * @see com.ost.mobilesdk.OstSdk
 */
public interface OstWorkFlowCallback {
    /**
     * Register device passed as parameter
     *
     * @param apiParams                    Register Device API parameters
     * @param ostDeviceRegisteredInterface To pass response
     */
    void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface);

    /**
     * Pin needed to check the authenticity of the user.
     * Developers should show pin dialog on this callback
     *
     * @param userId                Id of user whose password and pin are needed.
     * @param ostPinAcceptInterface To pass pin
     */
    void getPin(String userId, OstPinAcceptInterface ostPinAcceptInterface);

    /**
     * Inform SDK user about invalid pin
     * Developers should show invalid pin error and ask for pin again on this callback
     *
     * @param userId                Id of user whose password and pin are needed.
     * @param ostPinAcceptInterface to pass another pin
     */
    void invalidPin(String userId, OstPinAcceptInterface ostPinAcceptInterface);

    /**
     * Inform SDK user that entered pin is validated.
     * Developers should dismiss pin dialog on this callback
     *
     * @param userId Id of user whose pin and password has been validated.
     */
    void pinValidated(String userId);

    /**
     * Inform SDK user the the flow is complete
     *
     * @param ostWorkflowContext workflow type
     * @param ostContextEntity status of the flow
     * @see OstContextEntity
     */
    void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);

    /**
     * Inform SDK user that flow is interrupted with errorCode
     * Developers should dismiss pin dialog (if open) on this callback
     *
     * @param ostWorkflowContext workflow type
     * @param ostError reason of interruption
     */
    void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError);

    /**
     * Ask SDK user to show the provided QR code
     *
     * @param qrImage               QR code bitmap image
     * @param startPollingInterface To start polling
     */
    void showQR(Bitmap qrImage, OstStartPollingInterface startPollingInterface);

    /**
     * Device SDK is no more functional with corrupted data.
     * And it need to be reinitialized with new wallet key.
     */
    void deviceUnauthorized();

    /**
     * Show SDK user mnemonicsArray of the device address
     * @param mnemonics byte array of mnemonics
     */
    void showPaperWallet(byte[] mnemonics);

    /**
     * Inform SDK user about workflow core api call
     * @param ostWorkflowContext info about workflow type
     * @param ostContextEntity info about entity
     */
    void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);

    /**
     * Ask SDK user to verify data to proceed
     *
     * @param ostWorkflowContext       info about workflow type
     * @param ostContextEntity         info about entity
     * @param ostVerifyDataInterface to acknowledge workflow to proceed
     */
    void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface);
}