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
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import ost.com.demoapp.R;

public class SecondaryButton extends DemoAppButton {
    public SecondaryButton(Context context) {
        super(context);
    }

    public SecondaryButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SecondaryButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {
        super.defineUi(context, attrs, defStyleAttr);
        Resources res = getResources();
        setAllCaps(false);
        setTextColor(Color.parseColor("#34445b"));
        setLetterSpacing((float)-0.02);
        setLineSpacing(0, (float)0.3);
        setGravity(Gravity.CENTER);
        setTextAlignment(TEXT_ALIGNMENT_CENTER);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        setBackground(res.getDrawable(R.drawable.bg_button_hollow_round, null));
    }
}