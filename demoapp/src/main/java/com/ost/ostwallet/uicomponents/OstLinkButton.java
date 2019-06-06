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
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.ost.ostwallet.R;
import com.ost.ostwallet.uicomponents.uiutils.SizeUtil;

public class OstLinkButton extends OstButton {
    public OstLinkButton(Context context) {
        super(context);
    }

    public OstLinkButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OstLinkButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {
        super.defineUi(context, attrs, defStyleAttr);
        Resources res = getResources();
        setTextColor(getEnabledTextColor());
        setTextSize(SizeUtil.getTextSize(res, R.dimen.link_button_text_size));
        setBackgroundColor(res.getColor(R.color.link_button));
    }

    @Override
    protected int getDisabledTextColor() {
        return getResources().getColor(R.color.link_button_text_disabled);
    }

    @Override
    protected int getEnabledTextColor() {
        return getResources().getColor(R.color.link_button_text);
    }

    @Override
    protected Drawable getDisabledBackground() {
        return null;
    }

    @Override
    protected Drawable getEnabledBackground() {
        return null;
    }
}