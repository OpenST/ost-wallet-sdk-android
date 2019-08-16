/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.uicomponents;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.uicomponents.uiutils.SizeUtil;


public class OstSubHeaderTextView extends OstTextView {

    public OstSubHeaderTextView(Context context) {
        super(context);
    }

    public OstSubHeaderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OstSubHeaderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {
        super.defineUi(context, attrs, defStyleAttr);
        Resources res = getResources();
        setTextColor(res.getColor(R.color.sub_header_text));
        setTextAlignment(TEXT_ALIGNMENT_CENTER);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setLineSpacing(0, (float)1.5);
        setTextSize(SizeUtil.getTextSize(getResources(),R.dimen.sub_header_text));
    }
}
