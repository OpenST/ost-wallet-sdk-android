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
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;

import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.uiutils.Font;
import ost.com.demoapp.uicomponents.uiutils.FontFactory;
import ost.com.demoapp.uicomponents.uiutils.SizeUtil;

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
        Font font = FontFactory.getInstance(context, FontFactory.FONT.LATO);
        setTypeface(font.getRegular());

        float cornerRadius = SizeUtil.getTextSize(getResources(), R.dimen.primary_edit_text_radius);
        setBoxCornerRadii(cornerRadius, cornerRadius, cornerRadius, cornerRadius);
        setBoxStrokeColor(getResources().getColor(R.color.colorPrimary));

        mTextInputEditText = new TextInputEditText(context);
        mTextInputEditText.setTypeface(font.getRegular());
        mTextInputEditText.setTextSize(SizeUtil.getTextSize(getResources(), R.dimen.primary_edit_text_size));
        mTextInputEditText.setTextColor(getResources().getColor(R.color.primary_edittext_text));
        mTextInputEditText.setLetterSpacing((float) -0.02);
        mTextInputEditText.setLineSpacing(0, (float) 0.3);
        ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextInputEditText.setHeight((int) getResources().getDimension(R.dimen.primary_edittext_height));
        mTextInputEditText.setLayoutParams(params);
        mTextInputEditText.setHintTextColor(getResources().getColor(R.color.primary_edittext_hint_text));
        addView(mTextInputEditText);
    }

    public String getText() {
        return mTextInputEditText.getText().toString();
    }

    public void setText(String text) {
        mTextInputEditText.setText(text);
    }

    public void setRightDrawable(Drawable drawable) {
        mTextInputEditText.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                drawable,
                null
        );
    }

    public void setInputType(int inputType) {
       mTextInputEditText.setInputType(inputType);
    }
}