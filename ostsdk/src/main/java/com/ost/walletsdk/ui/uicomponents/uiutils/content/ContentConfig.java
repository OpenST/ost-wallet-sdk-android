package com.ost.walletsdk.ui.uicomponents.uiutils.content;

import android.content.Context;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ui.util.CommonUtils;

import org.json.JSONObject;

public class ContentConfig implements Content {

    private JSONObject mContentObject;
    private static Content contentConfig;

    private ContentConfig(Context context, JSONObject contentConfig) {
        mContentObject = new CommonUtils().deepMergeJSONObject(ContentDefault.getDefaultContent(context), contentConfig);
    }


    public static boolean isInitialized() {
        if ( null == contentConfig ) {
            return false;
        }
        return true;
    }

    public static void init(Context context, JSONObject contentObject) {
        contentConfig = new ContentConfig(context, contentObject);
    }

    public static Content getInstance() {
        if (null == contentConfig) {
            init(OstSdk.getContext(), new JSONObject());
        }
        return contentConfig;
    }

    @Override
    public JSONObject getStringConfig(String name) {
        return mContentObject.optJSONObject(name);
    }
}
