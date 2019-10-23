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
import android.content.res.ColorStateList;
import android.graphics.Color;
import com.ost.walletsdk.annotations.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.uicomponents.uiutils.SizeUtil;
import com.ost.walletsdk.ui.uicomponents.uiutils.theme.ThemeConfig;


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

    private RelativeLayout appBar;
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

        mainView.setBackgroundTintList(ColorStateList.valueOf(
                Color.parseColor(ThemeConfig.getInstance().getNavigationBar().getTintColor())
        ));

        appBar = (RelativeLayout) mainView.findViewById(R.id.tool_bar);

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
            String colorHash = ThemeConfig.getInstance().getIconConfig("back").getTintColor();
            imageView.setImageTintList(ColorStateList.valueOf(Color.parseColor(colorHash)));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ost_close, null));
            String colorHash = ThemeConfig.getInstance().getIconConfig("close").getTintColor();
            imageView.setImageTintList(ColorStateList.valueOf(Color.parseColor(colorHash)));
        }
        int pxPadding = new SizeUtil().dpToPx(20);
        imageView.setPadding(pxPadding, pxPadding, pxPadding, pxPadding);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        imageView.setLayoutParams(layoutParams);

        return imageView;
    }

    private ImageView getAppBarLogo(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setId(R.id.app_bar_title);
        imageView.setImageDrawable(ThemeConfig.getInstance().getDrawableConfig("nav_bar_logo_image").getDrawable(context));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imageView.setLayoutParams(layoutParams);

        return imageView;
    }

    public void showBackButton(boolean show) {
        mShowBackButton = show;
    }

    public void setBackButtonListener(OnClickListener onClickListener) {
        mBackButton.setOnClickListener(onClickListener);
    }

    public RelativeLayout getAppBar() {
        return appBar;
    }

    public LinearLayout getMainView() {
        return mainView;
    }
}