package ost.com.ostsdkui.uicomponents.uiutils.content;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import org.json.JSONObject;

public class DrawableConfig {

    private final String url;
    private final String name;

    DrawableConfig(JSONObject drawableObject) {
        this.url = drawableObject.optString("url");
        this.name = drawableObject.optString("name");
    }

    public Drawable getDrawable(Context context) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        return resources.getDrawable(resourceId, null);
    }
}
