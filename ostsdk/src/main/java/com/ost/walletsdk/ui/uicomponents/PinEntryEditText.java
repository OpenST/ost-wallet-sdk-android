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
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import com.ost.walletsdk.annotations.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.uicomponents.uiutils.theme.PinDrawable;
import com.ost.walletsdk.ui.uicomponents.uiutils.theme.ThemeConfig;

import java.util.ArrayList;
import java.util.List;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;


public class PinEntryEditText extends RelativeLayout {

    List<View> pins = new ArrayList<>();
    private AppCompatEditText invisiblePinEditText;
    private int pinLenght;
    private TextView.OnEditorActionListener mOnEditorActionListener;

    private int filledColor = Color.parseColor(ThemeConfig.getInstance().getPinViewConfig().getFilledColor());
    private int emptyColor = Color.parseColor(ThemeConfig.getInstance().getPinViewConfig().getEmptyColor());

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
        pinLenght = getResources().getInteger(R.integer.ost_type_your_pin_lenght);
        int inputType = getResources().getInteger(R.integer.ost_type_your_pin_input_type);
        int marginSize = getResources().getDimensionPixelSize(R.dimen.ost_type_your_pin_margins);
        int height = getResources().getDimensionPixelSize(R.dimen.ost_type_your_pin_size);
        int width = getResources().getDimensionPixelSize(R.dimen.ost_type_your_pin_size);

        setGravity(Gravity.CENTER);
        setBackgroundColor(Color.WHITE);
        setSize(this, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setupOnClickListener();
        setFocusableInTouchMode(false);

        setInvisibleEditText(pinLenght, inputType);

        LinearLayout pinLayout = new LinearLayout(getContext());
        pinLayout.setPadding(0,marginSize, 0, marginSize);
        pinLayout.setBackgroundColor(Color.WHITE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT, TRUE);
        pinLayout.setLayoutParams(params);
        pinLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(pinLayout);

        for (int i = 1; i <= pinLenght; i++) {
            View pin = new View(getContext());
            View space = new View(getContext());
            space.setBackgroundColor(Color.WHITE);
            setSize(space, width, height);
            setSize(pin, width, height);
            pins.add(pin);
            unfillPin(pin);
            pinLayout.addView(pin);

            if (!isLastPin(i, pinLenght)) {
                pinLayout.addView(space);
            }
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
        invisiblePinEditText = new AppCompatEditText(getContext());
        invisiblePinEditText.setTextSize(1);
        setSize(invisiblePinEditText, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT,TRUE);
        invisiblePinEditText.setLayoutParams(params);

        invisiblePinEditText.setRawInputType(Configuration.KEYBOARD_12KEY);
        invisiblePinEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
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
        if (v.getLayoutParams() instanceof MarginLayoutParams) {
            MarginLayoutParams p = (MarginLayoutParams) v.getLayoutParams();
            setMargins(v, left, p.topMargin, p.rightMargin, p.bottomMargin);
        }
    }

    public static void setMarginRight(View v, int right) {
        if (v.getLayoutParams() instanceof MarginLayoutParams) {
            MarginLayoutParams p = (MarginLayoutParams) v.getLayoutParams();
            setMargins(v, p.leftMargin, p.topMargin, right, p.bottomMargin);
        }
    }

    public static void setMargins(View v, int left, int top, int right, int bottom) {
        if (v.getLayoutParams() instanceof MarginLayoutParams) {
            MarginLayoutParams p = (MarginLayoutParams) v.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            v.requestLayout();
        }
    }

    private boolean isLastPin(int i, int pinSize) {
        return i == pinSize;
    }


    private void setupEditTextPinListener(final AppCompatEditText editText) {
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
                if (editable.length() == pinLenght) {
                    mOnEditorActionListener.onEditorAction(editText, EditorInfo.IME_ACTION_DONE, null);
                }
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
        PinDrawable pinDrawable = new PinDrawable();
        pinDrawable.setColorFilter(filledColor, PorterDuff.Mode.SRC_IN);
        pin.setBackground(pinDrawable);
    }

    private void unfillPin(View pin) {
        PinDrawable pinDrawable = new PinDrawable();
        pinDrawable.setColorFilter(emptyColor, PorterDuff.Mode.SRC_IN);
        pin.setBackground(pinDrawable);
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onEditorActionListener) {
        mOnEditorActionListener = onEditorActionListener;
        invisiblePinEditText.setOnEditorActionListener(onEditorActionListener);
    }

    public void setError(boolean show) {

    }

    public void setEnable(boolean enabled) {
        invisiblePinEditText.setEnabled(enabled);
        invisiblePinEditText.setFocusable(enabled);
        if (enabled) {
            setupOnClickListener();
        } else {
            invisiblePinEditText.setOnClickListener(null);
        }
    }

    public void setText(String text) {
        invisiblePinEditText.setText(text);
        updatePinView();
    }
}
