package com.ost.ostsdk.workflows;

import org.json.JSONObject;

/**
 * Sub Interface of
 * @see com.ost.ostsdk.workflows.OstBaseInterface
 * It declares deviceRegistered api of Workflows.
 */
public interface OstDeviceRegisteredInterface extends OstBaseInterface {
    /**
     * SDK user will use it to acknowledge device registration.
     * @param apiResponse Kit API response.
     */
    void deviceRegistered(JSONObject apiResponse);
}