/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.workflows.interfaces;

import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;

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