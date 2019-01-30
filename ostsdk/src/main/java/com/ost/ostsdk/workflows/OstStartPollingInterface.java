package com.ost.ostsdk.workflows;

/**
 * Sub Interface of
 * @see com.ost.ostsdk.workflows.OstBaseInterface
 * It declares startPolling api of Workflows.
 */
public interface OstStartPollingInterface extends OstBaseInterface {
    /**
     * SDK user will make SDK to start polling for status from kit.
     */
    void startPolling();
}