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

import org.json.JSONObject;

/**
 * Sub Interface of
 * @see OstBaseInterface
 * It declares deviceRegistered api of Workflows.
 */
public interface OstDeviceRegisteredInterface extends OstBaseInterface {
    /**
     * SDK user will use it to acknowledge device registration.
     * @param apiResponse OST Platform API response.
     */
    void deviceRegistered(JSONObject apiResponse);
}