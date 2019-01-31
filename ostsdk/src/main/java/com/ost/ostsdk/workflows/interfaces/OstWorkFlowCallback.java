package com.ost.ostsdk.workflows.interfaces;

import android.graphics.Bitmap;

import com.ost.ostsdk.workflows.OstContextEntity;
import com.ost.ostsdk.workflows.OstError;
import com.ost.ostsdk.workflows.OstWorkFlowFactory;

import org.json.JSONObject;

/**
 * OstWorkFlowCallback implemented by SDK user to perform prerequisites task.
 * These tasks are assigned by SDK workflows with help of callbacks.
 * @see OstWorkFlowFactory
 */
public interface OstWorkFlowCallback {
    /**
     * Register device passed as parameter
     * @param apiParams Register Device API parameters
     * @param ostDeviceRegisteredInterface To pass response
     */
    void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface);

    /**
     * Pin needed to check the authenticity of the user.
     * Developers should show pin dialog on this callback
     * @param userId Id of user whose password and pin are needed.
     * @param ostPinAcceptInterface To pass pin
     */
    void getPin(String userId, OstPinAcceptInterface ostPinAcceptInterface);

    /**
     * Inform SDK user about invalid pin
     * Developers should show invalid pin error and ask for pin again on this callback
     * @param userId Id of user whose password and pin are needed.
     * @param ostPinAcceptInterface to pass another pin
     */
    void invalidPin(String userId, OstPinAcceptInterface ostPinAcceptInterface);

    /**
     * Inform SDK user that entered pin is validated.
     * Developers should dismiss pin dialog on this callback
     * @param userId Id of user whose pin and password has been validated.
     */
    void pinValidated(String userId);

    /**
     * Inform SDK user the the flow is complete
     * @param ostContextEntity status of the flow
     * @see OstContextEntity
     */
    void flowComplete(OstContextEntity ostContextEntity);

    /**
     * Inform SDK user that flow is interrupted with errorCode
     * Developers should dismiss pin dialog (if open) on this callback
     * @param ostError reason of interruption
     */
    void flowInterrupt(OstError ostError);

    /**
     * Ask SDK user to determine workflow how to add device
     * @param addDeviceFlowInterface To device type add device flow
     */
    void determineAddDeviceWorkFlow(OstAddDeviceFlowInterface addDeviceFlowInterface);
    /**
     * Ask SDK user to show the provided QR code
     * @param startPollingInterface To start polling
     * @param qrImage QR code bitmap image
     */
    void showQR(OstStartPollingInterface startPollingInterface, Bitmap qrImage);

    /**
     * Wallet words needed to recover user wallet.
     * @param ostWalletWordsAcceptInterface to pass 12 wallet words
     */
    void getWalletWords(OstWalletWordsAcceptInterface ostWalletWordsAcceptInterface);

    /**
     * Inform SDK user about invalid pin
     * @param ostWalletWordsAcceptInterface to pass 12 wallet words
     */
    void invalidWalletWords(OstWalletWordsAcceptInterface ostWalletWordsAcceptInterface);

    /**
     * Inform SDK user that entered 12 wallet words is validated
     */
    void walletWordsValidated();

    /**
     * Device SDK is no more functional with corrupted data.
     * And it need to be reinitialized with new wallet key.
     */
    void deviceUnauthorized();
}