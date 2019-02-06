package com.ost.mobilesdk.workflows.interfaces;

/**
 * Sub Interface of
 * @see OstPinAcceptInterface
 * @see OstWalletWordsAcceptInterface
 */

public interface OstAddDeviceFlowInterface extends OstPinAcceptInterface, OstWalletWordsAcceptInterface {
    /**
     * SDK user will use it to start QRCode flow.
     */
    void QRCodeFlow();
}