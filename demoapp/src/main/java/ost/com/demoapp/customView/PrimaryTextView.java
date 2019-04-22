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
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import ost.com.demoapp.R;

public class PrimaryTextView extends DemoAppTextView {
    public PrimaryTextView(Context context) {
        super(context);
    }

    public PrimaryTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrimaryTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {
        super.defineUi(context, attrs, defStyleAttr);
        Resources res = getResources();
        setTextColor(res.getColor(R.color.text_view_primary));
        setTextAlignment(TEXT_ALIGNMENT_CENTER);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setLineSpacing(0, (float)2);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
    }
}
