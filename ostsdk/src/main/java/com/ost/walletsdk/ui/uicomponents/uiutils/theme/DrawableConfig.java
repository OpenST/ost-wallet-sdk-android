package com.ost.walletsdk.ui.uicomponents.uiutils.theme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import org.json.JSONObject;

public class DrawableConfig {

    private final String name;
    private final String tintColor;

    DrawableConfig(JSONObject drawableObject) {
        this.name = drawableObject.optString("asset_name");
        this.tintColor = drawableObject.optString("tint_color");
    }

    public Drawable getDrawable(Context context) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        return resources.getDrawable(resourceId, null);
    }

    public String getTintColor() {
       return this.tintColor;
    }
}