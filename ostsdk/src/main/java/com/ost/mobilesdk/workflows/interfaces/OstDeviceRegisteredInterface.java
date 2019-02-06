package com.ost.mobilesdk.workflows.interfaces;

import org.json.JSONObject;

/**
 * Sub Interface of
 * @see OstBaseInterface
 * It declares deviceRegistered api of Workflows.
 */
public interface OstDeviceRegisteredInterface extends OstBaseInterface {
    /**
     * SDK user will use it to acknowledge device registration.
     * @param apiResponse Kit API response.
     */
    void deviceRegistered(JSONObject apiResponse);
}