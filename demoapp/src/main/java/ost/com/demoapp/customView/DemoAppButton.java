/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.customView;

import android.content.Context;
import android.util.AttributeSet;

import ost.com.demoapp.util.FontCache;

public class DemoAppButton extends android.support.v7.widget.AppCompatButton {
    public DemoAppButton(Context context) {
        super(context);
        this.setTypeface(FontCache.get(context, "fonts/Quasimoda-semi-bold"));
        defineUi(context, null, 0);
    }

    public DemoAppButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(FontCache.get(context, "fonts/Quasimoda-semi-bold"));
        defineUi(context, attrs, 0);
    }

    public DemoAppButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(FontCache.get(context, "fonts/Quasimoda-semi-bold"));
        defineUi(context, attrs, defStyleAttr);
    }

    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {

    }
}