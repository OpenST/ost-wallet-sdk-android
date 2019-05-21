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
import android.util.AttributeSet;

import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.uiutils.SizeUtil;

public class OstListButton extends OstSecondaryButton {
    public OstListButton(Context context) {
        super(context);
    }

    public OstListButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OstListButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {
        super.defineUi(context, attrs, defStyleAttr);
        Resources res = getResources();
        setTextSize(SizeUtil.getTextSize(res, R.dimen.view_cell_sub_text));
    }
}