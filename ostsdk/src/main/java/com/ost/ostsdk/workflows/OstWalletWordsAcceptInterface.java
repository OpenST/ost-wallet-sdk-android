package com.ost.ostsdk.workflows;

import java.util.List;

/**
 * Sub Interface of
 * @see com.ost.ostsdk.workflows.OstBaseInterface
 * It declares walletWordsEntered api of Workflows.
 */

public interface OstWalletWordsAcceptInterface extends OstBaseInterface {
    /**
     * SDK user will use it to pass wallet 12 words to SDK.
     * @param wordList List of wallet 12 words
     */
    void walletWordsEntered(List<String> wordList);
}