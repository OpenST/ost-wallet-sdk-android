package com.ost.walletsdk.ui.uicomponents.uiutils.theme;

import android.graphics.Color;
import android.widget.TextView;

import org.json.JSONObject;

public class EditTextUIConfig extends UIConfig {
    private final UIConfig placeHolder;

    EditTextUIConfig(JSONObject jsonObject) {
        super(jsonObject);
        this.placeHolder = new UIConfig(jsonObject.optJSONObject("placeholder"));
    }

    @Override
    public void apply(TextView textView) {
        super.apply(textView);
        textView.setHintTextColor(Color.parseColor(placeHolder.getColor()));
    }
}