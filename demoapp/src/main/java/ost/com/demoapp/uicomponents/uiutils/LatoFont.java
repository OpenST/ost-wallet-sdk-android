/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.uicomponents.uiutils;

import android.content.Context;
import android.graphics.Typeface;

class LatoFont implements Font {

    private final Context mContext;

    LatoFont(Context context) {
        mContext = context;
    }
    @Override
    public Typeface getRegular() {
        return getFromCache("fonts/Lato-Regular.ttf");
    }

    @Override
    public Typeface getLightItalic() {
        return getFromCache("fonts/Lato-LightItalic.ttf");
    }

    @Override
    public Typeface getLight() {
        return getFromCache("fonts/Lato-Light.ttf");
    }

    @Override
    public Typeface getItalic() {
        return getFromCache("fonts/Lato-Italic.ttf");
    }

    @Override
    public Typeface getBoldItalic() {
        return getFromCache("fonts/Lato-BoldItalic.ttf");
    }

    @Override
    public Typeface getBold() {
        return getFromCache("fonts/Lato-Bold.ttf");
    }

    private Typeface getFromCache(String fontPath) {
        return FontCache.get(mContext, fontPath);
    }
}
