/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.uicomponents;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.uiutils.SizeUtil;

public class OstSecondaryButton extends OstButton {

    public OstSecondaryButton(Context context) {
        super(context);
    }

    public OstSecondaryButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OstSecondaryButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {
        super.defineUi(context, attrs, defStyleAttr);
        Resources res = getResources();
        setTextColor(getEnabledTextColor());
        setTextSize(SizeUtil.getTextSize(res, R.dimen.secondary_button_text_size));
        setBackground(getEnabledBackground());
    }

    @Override
    protected int getDisabledTextColor() {
        return getResources().getColor(R.color.secondary_button_text_disabled);
    }

    @Override
    protected int getEnabledTextColor() {
        return getResources().getColor(R.color.secondary_button_text);
    }

    @Override
    protected Drawable getDisabledBackground() {
        return getResources().getDrawable(R.drawable.bg_secondary_button_disabled, null);
    }

    @Override
    protected Drawable getEnabledBackground() {
        return getResources().getDrawable(R.drawable.bg_secondary_button, null);
    }
}