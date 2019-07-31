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
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;


public class AppBar extends LinearLayout {

    private ImageView mBackButton;
    private ImageView mImageView;
    private boolean mShowBackButton;

    public AppBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AppBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Toolbar appBar;
    private LinearLayout mainView;

    public AppBar(Context context) {
        this(context, true);
    }

    public AppBar(Context context, boolean showBackButton) {
        super(context);
        setId(R.id.app_bar);
        showBackButton(showBackButton);

        LayoutInflater inflater = LayoutInflater.from(context);
        mainView = (LinearLayout) inflater.inflate(R.layout.ost_app_bar, this, false);
        addView(mainView);

        appBar = (Toolbar) mainView.findViewById(R.id.tool_bar);

        mImageView = getAppBarLogo(context);

        mBackButton = getBackButton(context);

        appBar.addView(mImageView);
        appBar.addView(mBackButton);
    }

    public static AppBar newInstance(Context context, boolean showBackButton) {
        AppBar appBar = new AppBar(context, showBackButton);
        return appBar;
    }

    private ImageView getBackButton(Context context) {
        ImageView imageView = new ImageView(context);
        if (mShowBackButton) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ost_back, null));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ost_close, null));
        }

        imageView.setLayoutParams(new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL));
        return imageView;
    }

    private ImageView getAppBarLogo(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setId(R.id.app_bar_title);
        imageView.setImageDrawable(ContentConfig.getInstance().getDrawableConfig("image_nav_bar_logo").getDrawable(context));

        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        layoutParams.leftMargin = (int) context.getResources().getDimension(R.dimen.dp_30);
        layoutParams.rightMargin = (int) context.getResources().getDimension(R.dimen.dp_30);
        imageView.setLayoutParams(layoutParams);

        return imageView;
    }

    public void showBackButton(boolean show) {
        mShowBackButton = show;
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