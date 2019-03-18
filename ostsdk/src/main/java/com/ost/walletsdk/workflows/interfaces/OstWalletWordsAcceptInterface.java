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

import java.util.List;

/**
 * Sub Interface of
 * @see OstBaseInterface
 * It declares walletWordsEntered api of Workflows.
 */

public interface OstWalletWordsAcceptInterface extends OstBaseInterface {
    /**
     * SDK user will use it to pass wallet 12 words to SDK.
     * @param wordList List of wallet 12 words
     */
    void walletWordsEntered(List<String> wordList);
}