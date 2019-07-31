package com.ost.walletsdk.ui.uicomponents.uiutils.content;

import org.json.JSONObject;

public interface Content {
    DrawableConfig getDrawableConfig(String name);

    JSONObject getStringConfig(String name);
}