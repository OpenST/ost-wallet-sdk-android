/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.uicomponents;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;

import com.ost.ostwallet.R;
import com.ost.ostwallet.uicomponents.uiutils.Font;
import com.ost.ostwallet.uicomponents.uiutils.FontFactory;

public abstract class OstButton extends android.support.v7.widget.AppCompatButton {
    public OstButton(Context context) {
        super(context);
        defineUi(context, null, 0);
    }

    public OstButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        defineUi(context, attrs, 0);
    }

    public OstButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defineUi(context, attrs, defStyleAttr);
    }

    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {
        Font font = FontFactory.getInstance(context, FontFactory.FONT.LATO);
        setTextAppearance(context, R.style.Widget_AppCompat_Button);
        setTypeface(font.getBold());
        setAllCaps(false);
        setLetterSpacing((float) -0.02);
        setLineSpacing(0, (float) 0.3);
        setGravity(Gravity.CENTER);
        setTextAlignment(TEXT_ALIGNMENT_CENTER);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setBackground(enabled ? getEnabledBackground() : getDisabledBackground());
        setTextColor(enabled ? getEnabledTextColor() : getDisabledTextColor());
    }

    protected abstract int getDisabledTextColor();

    protected abstract int getEnabledTextColor();

    protected abstract Drawable getDisabledBackground();

    protected abstract Drawable getEnabledBackground();
}