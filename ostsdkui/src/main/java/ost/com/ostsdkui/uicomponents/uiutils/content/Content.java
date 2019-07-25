package ost.com.ostsdkui.uicomponents.uiutils.content;

import android.graphics.drawable.Drawable;

public interface Content {
    DrawableConfig getDrawableConfig(String name);

    StringConfig getStringConfig(String name);
}