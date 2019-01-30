package com.ost.ostsdk.workflows.interfaces;

/**
 * Sub Interface of
 * @see OstBaseInterface
 * It declares startPolling api of Workflows.
 */
public interface OstStartPollingInterface extends OstBaseInterface {
    /**
     * SDK user will make SDK to start polling for status from kit.
     */
    void startPolling();
}