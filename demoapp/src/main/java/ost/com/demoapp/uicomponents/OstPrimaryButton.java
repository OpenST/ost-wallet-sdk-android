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
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.uiutils.SizeUtil;

public class OstPrimaryButton extends OstButton {

    public OstPrimaryButton(Context context) {
        super(context);
    }

    public OstPrimaryButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OstPrimaryButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {
        super.defineUi(context, attrs, defStyleAttr);
        Resources res = getResources();
        setTextColor(res.getColor(R.color.primary_button_text));
        setTextSize(SizeUtil.getTextSize(res, R.dimen.primary_button_text_size));
        setBackground(res.getDrawable(R.drawable.bg_primary_button, null));
    }
}