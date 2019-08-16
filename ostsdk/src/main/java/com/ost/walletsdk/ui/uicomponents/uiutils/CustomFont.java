/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.uicomponents.uiutils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.ost.walletsdk.ui.uicomponents.uiutils.theme.ThemeConfig;

class CustomFont implements Font {

    private final Context mContext;

    CustomFont(Context context) {
        mContext = context;
    }

    @Override
    public Typeface getFont(String font) {
        String fontPath = ThemeConfig.getInstance().getFontRelativePath(font);
        if (TextUtils.isEmpty(fontPath)) {
            return null;
        }
        return FontCache.get(mContext, fontPath);
    }
}