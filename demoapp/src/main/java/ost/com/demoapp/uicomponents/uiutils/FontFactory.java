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

public class FontFactory {
    public enum FONT {
        LATO
    }
    public static Font getInstance(Context context, FONT font) {
        if (FONT.LATO.equals(font)) {
            return new LatoFont(context);
        }
        return new LatoFont(context);
    }
}
