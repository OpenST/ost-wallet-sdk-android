package com.ost.walletsdk.ui.uicomponents.uiutils.content;

import org.json.JSONObject;

public class StringConfig {

    private final String name;
    private final String url;

    StringConfig(JSONObject stringObject) {
        this.name = stringObject.optString("name");
        this.url = stringObject.optString("url");

    }

    public String getString() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }
}
