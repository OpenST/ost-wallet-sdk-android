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
import com.ost.walletsdk.annotations.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.ost.ostwallet.R;
import com.ost.ostwallet.uicomponents.uiutils.Font;
import com.ost.ostwallet.uicomponents.uiutils.FontFactory;

public class OstPrimaryEditTextView extends RelativeLayout {
    private TextInputEditText mTextInputEditText;
    private TextInputLayout mTextInputLayout;

    public OstPrimaryEditTextView(Context context) {
        super(context);
        defineUi(context, null, 0);
    }

    public OstPrimaryEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        defineUi(context, attrs, 0);
    }

    public OstPrimaryEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defineUi(context, attrs, defStyleAttr);
    }

    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayoutView = inflater.inflate(R.layout.material_edit_text, this, true);

        mTextInputLayout = inflatedLayoutView.findViewById(R.id.text_input_layout);
        mTextInputEditText = inflatedLayoutView.findViewById(R.id.text_input_edit_text);

        Font font = FontFactory.getInstance(context, FontFactory.FONT.LATO);
        mTextInputLayout.setTypeface(font.getRegular());
        mTextInputEditText.setTypeface(font.getRegular());
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

    public void setHintText(String hintText) {
        mTextInputLayout.setHint(hintText);
    }

    public void diasbleInput(){
        mTextInputEditText.setInputType(InputType.TYPE_NULL);
        mTextInputEditText.setTextIsSelectable(false);
        mTextInputEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return true;  // Blocks input from hardware keyboards.
            }
        });
    }

    public void setOnTextChangeListener(TextWatcher textWatcher){
        mTextInputEditText.addTextChangedListener(textWatcher);
    }

    public void setOnFocusListener(View.OnFocusChangeListener listener){
        mTextInputEditText.setOnFocusChangeListener(listener);
    }

    @Override
    public void setOnClickListener(@Nullable View.OnClickListener l) {
        super.setOnClickListener(l);
        mTextInputLayout.setOnClickListener(l);
        mTextInputEditText.setOnClickListener(l);
    }

    public void showErrorString(String errorString) {
        mTextInputLayout.setErrorEnabled(true);
        mTextInputLayout.setError(errorString);
    }
}