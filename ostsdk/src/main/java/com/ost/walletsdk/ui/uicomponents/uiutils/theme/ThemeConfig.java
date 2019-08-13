package com.ost.walletsdk.ui.uicomponents.uiutils.theme;

import android.content.Context;

import com.ost.walletsdk.ui.util.CommonUtils;

import org.json.JSONObject;


public class ThemeConfig implements Theme {

    private JSONObject mThemeObject;
    private static Theme themeConfig;

    private ThemeConfig(Context context, JSONObject themeObject) {
        mThemeObject = new CommonUtils().deepMergeJSONObject(ThemeDefault.getDefaultTheme(context), themeObject);
    }

    public static void init(Context context, JSONObject themeObject) {
        themeConfig = new ThemeConfig(context, themeObject);
    }

    public static Theme getInstance() {
        if (null == themeConfig) {
            throw new RuntimeException("ThemeConfig is not initialized");
        }
        return themeConfig;
    }

    @Override
    public UIConfig H1() {
        return new UIConfig(mThemeObject.optJSONObject("h1"));
    }

    @Override
    public UIConfig H2() {
        return new UIConfig(mThemeObject.optJSONObject("h2"));
    }

    @Override
    public UIConfig H3() {
        return new UIConfig(mThemeObject.optJSONObject("h3"));
    }

    @Override
    public UIConfig H4() {
        return new UIConfig(mThemeObject.optJSONObject("h4"));
    }

    @Override
    public UIConfig C1() {
        return new UIConfig(mThemeObject.optJSONObject("c1"));
    }

    @Override
    public UIConfig C2() {
        return new UIConfig(mThemeObject.optJSONObject("c2"));
    }

    @Override
    public UIConfig B1() {
        return new UIConfig(mThemeObject.optJSONObject("b1"));
    }

    @Override
    public UIConfig B2() {
        return new UIConfig(mThemeObject.optJSONObject("b2"));
    }

    @Override
    public UIConfig B3() {
        return new UIConfig(mThemeObject.optJSONObject("b3"));
    }

    @Override
    public UIConfig B4() {
        return new UIConfig(mThemeObject.optJSONObject("b4"));
    }

    @Override
    public DrawableConfig getDrawableConfig(String image_name) {
        return new DrawableConfig(mThemeObject.optJSONObject(image_name));
    }

    @Override
    public DrawableConfig getIconConfig(String imageName) {
        return new DrawableConfig(mThemeObject.optJSONObject("icons").optJSONObject(imageName));
    }

    @Override
    public String getFontRelativePath(String font) {
        JSONObject fontConfig = mThemeObject.optJSONObject("fonts");
        if (null == fontConfig) return null;
        return fontConfig.optString(font);
    }

    @Override
    public NavigationConfig getNavigationBar() {
        return new NavigationConfig(mThemeObject.optJSONObject("navigation_bar"));
    }

    @Override
    public PinViewConfig getPinViewConfig() {
        return new PinViewConfig(mThemeObject.optJSONObject("pin_input"));
    }
}
