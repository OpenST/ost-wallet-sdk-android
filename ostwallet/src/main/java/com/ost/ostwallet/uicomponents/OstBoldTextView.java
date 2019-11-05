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
import android.graphics.Typeface;
import com.ost.walletsdk.annotations.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;

import com.ost.ostwallet.uicomponents.uiutils.Font;
import com.ost.ostwallet.uicomponents.uiutils.FontFactory;

public class OstBoldTextView extends OstTextView {
    public OstBoldTextView(Context context) {
        super(context);
    }

    public OstBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OstBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {
        super.defineUi(context, attrs, defStyleAttr);
        Resources res = getResources();
        setTextAlignment(TEXT_ALIGNMENT_CENTER);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setLineSpacing(0, (float)1.5);
    }

    @Override
    public void setTypeface(@Nullable Typeface tf) {
        Font font = FontFactory.getInstance(getContext(), FontFactory.FONT.LATO);
        super.setTypeface(font.getBold());
    }
}
