package com.ost.walletsdk.ui.uicomponents.uiutils.theme;

import org.json.JSONObject;

public class PinViewConfig {
    public String getEmptyColor() {
        return emptyColor;
    }

    public String getFilledColor() {
        return filledColor;
    }

    private final String emptyColor;
    private final String filledColor;

    public PinViewConfig(JSONObject navigationObject) {
        this.emptyColor = navigationObject.optString("empty_color");
        this.filledColor = navigationObject.optString("filled_color");
    }
}