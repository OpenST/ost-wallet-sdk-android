package com.ost.mobilesdk.workflows.interfaces;

/**
 * Sub Interface of
 * @see OstBaseInterface
 * It declares pinEntered api of Workflows.
 */
public interface OstPinAcceptInterface extends OstBaseInterface {
    /**
     * SDK user will use it to pass user pin to SDK.
     * @param uPin user pin passed from Application
     * @param appUserPassword Application Provided Password for the user.
     */
    void pinEntered(String uPin, String appUserPassword);
}