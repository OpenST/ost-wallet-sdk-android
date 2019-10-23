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
import android.content.res.TypedArray;
import android.graphics.Color;
import com.ost.walletsdk.annotations.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ost.ostwallet.R;

public class AppBar extends LinearLayout {

    private ImageView mBackButton;
    private TextView mTextView;

    public AppBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AppBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Toolbar appBar;
    private LinearLayout mainView;

    public AppBar(Context context) {
        super(context);
        setId(R.id.app_bar);

        LayoutInflater inflater = LayoutInflater.from(context);
        mainView = (LinearLayout) inflater.inflate(R.layout.app_bar, this, false);
        addView(mainView);

        appBar = (Toolbar) mainView.findViewById(R.id.tool_bar);

        mTextView = getTitleView(context);

        mBackButton = getBackButton(context);

        appBar.addView(mTextView);
        appBar.addView(mBackButton);
    }

    public static AppBar newInstance(Context context, String title, boolean showBackButton) {
        AppBar appBar = new AppBar(context);
        appBar.setTitle(title);
        appBar.showBackButton(showBackButton);
        return appBar;
    }

    private ImageView getBackButton(Context context) {
        ImageView imageView = new ImageView(context);
        TypedArray a = context.obtainStyledAttributes(R.style.AppTheme, new int[] {R.attr.homeAsUpIndicator});
        int attributeResourceId = a.getResourceId(0, 0);
        imageView.setImageDrawable(getResources().getDrawable(attributeResourceId,null));
        a.recycle();

        imageView.setLayoutParams(new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL));
        return imageView;
    }

    private TextView getTitleView(Context context) {
        TextView textView = new OstTextView(context);
        textView.setId(R.id.app_bar_title);
        textView.setText(getResources().getText(R.string.app_name));
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setBackgroundResource(android.R.color.transparent);
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);

        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        layoutParams.leftMargin = (int) context.getResources().getDimension(R.dimen.dp_30);
        layoutParams.rightMargin = (int) context.getResources().getDimension(R.dimen.dp_30);
        textView.setLayoutParams(layoutParams);

        return textView;
    }

    public void setTitle(String title) {
        mTextView.setText(title);
    }

    public void showBackButton(boolean show) {
        mBackButton.setVisibility(show ? VISIBLE : GONE );
    }

    public void setBackButtonListener(OnClickListener onClickListener) {
        mBackButton.setOnClickListener(onClickListener);
    }

    public Toolbar getAppBar() {
        return appBar;
    }

    public LinearLayout getMainView() {
        return mainView;
    }
}
