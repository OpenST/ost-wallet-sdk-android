package com.ost.mobilesdk.workflows.interfaces;

import com.ost.mobilesdk.ecKeyInteracts.UserPassphrase;

/**
 * Sub Interface of
 * @see OstBaseInterface
 * It declares pinEntered api of Workflows.
 */
public interface OstPinAcceptInterface extends OstBaseInterface {
    /**
     * SDK user will use it to pass user pin to SDK.
     * @param passphrase recovery passphrase of the user.
     */
    void pinEntered(UserPassphrase passphrase);
}