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
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

import ost.com.demoapp.util.FontCache;

public class PrimaryEditTextView extends TextInputLayout {
    private TextInputEditText mTextInputEditText;

    public PrimaryEditTextView(Context context) {
        super(context);
        defineUi(context, null, 0);
    }

    public PrimaryEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        defineUi(context, attrs, 0);
    }

    public PrimaryEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defineUi(context, attrs, defStyleAttr);
    }

    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {
        this.setTypeface(FontCache.get(context, "fonts/Quasimoda-semi-bold"));
        mTextInputEditText = new TextInputEditText(context, attrs);
        mTextInputEditText.setPadding(20,50,20,50);
        addView(mTextInputEditText);
    }

    public String getText() {
        return mTextInputEditText.getText().toString();
    }

    public void setText(String text) {
         mTextInputEditText.setText(text);
    }
}