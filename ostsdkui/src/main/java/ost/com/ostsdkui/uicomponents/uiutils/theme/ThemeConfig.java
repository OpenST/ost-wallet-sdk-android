package ost.com.ostsdkui.uicomponents.uiutils.theme;

import android.content.Context;

import org.json.JSONObject;

import ost.com.ostsdkui.util.CommonUtils;

public class ThemeConfig implements Theme {

    private JSONObject mThemeObject;
    private static Theme themeConfig;

    private ThemeConfig(Context context, JSONObject themeObject) {
        mThemeObject = new CommonUtils().deepMergeJSONObject(themeObject, ThemeDefault.getDefaultTheme(context));
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
}
