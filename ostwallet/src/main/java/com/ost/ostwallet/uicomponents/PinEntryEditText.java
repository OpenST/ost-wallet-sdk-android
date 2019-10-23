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
import android.content.res.Configuration;
import com.ost.walletsdk.annotations.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.ost.ostwallet.R;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;


public class PinEntryEditText extends LinearLayout {

    List<View> pins = new ArrayList<>();
    private EditText invisiblePinEditText;
    private int pinLenght;

    public PinEntryEditText(Context context) {
        super(context);
        init();
    }

    public PinEntryEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PinEntryEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        pinLenght = getResources().getInteger(R.integer.type_your_pin_lenght);
        int inputType = getResources().getInteger(R.integer.type_your_pin_input_type);
        int marginSize = getResources().getDimensionPixelSize(R.dimen.type_your_pin_margins);
        int height = getResources().getDimensionPixelSize(R.dimen.type_your_pin_size);
        int width = getResources().getDimensionPixelSize(R.dimen.type_your_pin_size);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setSize(this, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setupOnClickListener();
        setFocusableInTouchMode(false);

        setInvisibleEditText(pinLenght, inputType);

        for (int i = 1; i <= pinLenght; i++) {
            View pin = new View(getContext());
            setSize(pin, width, height);
            setMarginLeft(pin, marginSize);

            if (isLastPin(i, pinLenght)) {
                setMarginRight(pin, marginSize);
            }
            pins.add(pin);
            unfillPin(pin);
            addView(pin);
        }
    }

    private void setupOnClickListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPinEditTextFocus();
                updatePinView();
            }
        });
    }

    private void requestPinEditTextFocus() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && invisiblePinEditText != null) {
            imm.showSoftInput(invisiblePinEditText, 0);
            invisiblePinEditText.requestFocus();
        }
    }

    private void setInvisibleEditText(int pinLenght, int inputType) {
        invisiblePinEditText = new EditText(getContext());
        setSize(invisiblePinEditText, 0, 0);
        invisiblePinEditText.setRawInputType(Configuration.KEYBOARD_12KEY);
        invisiblePinEditText.setInputType(InputType.TYPE_CLASS_NUMBER );
        invisiblePinEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(pinLenght)});
        invisiblePinEditText.setImeOptions(IME_ACTION_DONE);
        addView(invisiblePinEditText);
        setupEditTextPinListener(invisiblePinEditText);
        requestPinEditTextFocus();
    }

    public String getText() {
        return invisiblePinEditText.getText().toString();
    }
    private void setSize(View v, int width, int height) {
        v.setLayoutParams(new LayoutParams(width, height));
    }

    public static void setMarginLeft(View v, int left) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            setMargins(v, left, p.topMargin, p.rightMargin, p.bottomMargin);
        }
    }

    public static void setMarginRight(View v, int right) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            setMargins(v, p.leftMargin, p.topMargin, right, p.bottomMargin);
        }
    }

    public static void setMargins(View v, int left, int top, int right, int bottom) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            v.requestLayout();
        }
    }

    private boolean isLastPin(int i, int pinSize) {
        return i == pinSize;
    }


    private void setupEditTextPinListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            int lastTextLenght = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lastTextLenght = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatePinView();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void updatePinView() {
        int textLengthAfter = invisiblePinEditText.length();
        int index = 0;
        for (;index < textLengthAfter;index++) {
            fillPin(pins.get(index));
        }
        for (;index < pinLenght; index++) {
            unfillPin(pins.get(index));
        }
    }

    private boolean hasFinishedTyping(int textLenghtAfter) {
        return textLenghtAfter == pinLenght;
    }

    private boolean hasNewPin(int textSizeBefore, int size) {
        return textSizeBefore < size;
    }

    private void fillPin(View pin) {
        pin.setBackground(getResources().getDrawable(R.drawable.bg_pin_round_fill, null));
    }

    private void unfillPin(View pin) {
        pin.setBackground(getResources().getDrawable(R.drawable.bg_pin_round_unfill, null));
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onEditorActionListener) {
        invisiblePinEditText.setOnEditorActionListener(onEditorActionListener);
    }

    public void setError(boolean show) {

    }
}