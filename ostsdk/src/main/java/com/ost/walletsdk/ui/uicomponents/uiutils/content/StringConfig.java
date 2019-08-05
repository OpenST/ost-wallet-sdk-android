package com.ost.walletsdk.ui.uicomponents.uiutils.content;

import org.json.JSONObject;

public class StringConfig {

    private final String name;
    private final String url;
    private final String color;
    private final String font;

    StringConfig(JSONObject stringObject) {
        this.name = stringObject.optString("text");
        this.url = stringObject.optString("url");
        this.color = stringObject.optString("color");
        this.font = stringObject.optString("font");
    }

    public static StringConfig instance(JSONObject jsonObject) {
        return new StringConfig(jsonObject);
    }

    public String getString() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public String getColor() {
        return color;
    }

    public String getFont() {
        return font;
    }
}
