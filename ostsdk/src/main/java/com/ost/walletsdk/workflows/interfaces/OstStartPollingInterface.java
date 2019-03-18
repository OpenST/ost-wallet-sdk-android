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