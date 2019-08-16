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
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.ost.walletsdk.R;

public class OstTextView extends AppCompatTextView {
    public OstTextView(Context context) {
        super(context);
        defineUi(context, null, 0);
    }

    public OstTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        defineUi(context, attrs, 0);
    }

    public OstTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defineUi(context, attrs, defStyleAttr);
    }

    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {
    }

    public void setDisabled(){
        this.setTextColor(getResources().getColor(R.color.primary_button_disabled));
    }
}