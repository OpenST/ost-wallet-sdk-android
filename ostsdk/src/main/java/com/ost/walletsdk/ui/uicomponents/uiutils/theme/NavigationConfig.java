package com.ost.walletsdk.ui.uicomponents.uiutils.theme;

import org.json.JSONObject;

public class NavigationConfig {

    private final String tintColor;

    NavigationConfig(JSONObject navigationObject) {
        this.tintColor = navigationObject.optString("tint_color");
    }

    public String getTintColor() {
       return this.tintColor;
    }
}