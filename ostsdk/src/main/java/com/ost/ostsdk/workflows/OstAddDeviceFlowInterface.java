package com.ost.ostsdk.workflows;

/**
 * Sub Interface of
 * @see com.ost.ostsdk.workflows.OstPinAcceptInterface
 * @see com.ost.ostsdk.workflows.OstWalletWordsAcceptInterface
 */

interface OstAddDeviceFlowInterface extends OstPinAcceptInterface, OstWalletWordsAcceptInterface {
    /**
     * SDK user will use it to start QRCode flow.
     */
    void QRCodeFlow();
}